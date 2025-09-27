unit RedisUtils;

interface

uses
  Winapi.Windows,
  System.Classes,
  System.SysUtils,
  System.SyncObjs,
  System.Generics.Collections,
  System.Threading,
  Redis.Client,
  Redis.Commons,
  Redis.NetLib.INDY,
  Redis.Config,
  LogUtils;

type
  IKeyValueStore = interface
    ['{D840CE1C-A7F0-49A2-9CA9-E4EE913FB18E}']
    function Get(const Key: string): string;
    function SetEX(const Key, Value: string; const TTLSeconds: Integer): Boolean;
    function SetNXEX(const Key, Value: string; const TTLSeconds: Integer): Boolean;
    function Del(const Key: string): Integer;
    function Exists(const Key: string): Boolean;
    function Expire(const Key: string; const TTLSeconds: Integer): Boolean;
  end;

  TRedis = class
  private
    FRedisClient: IRedisClient;
    FRedisSubscriber: IRedisClient;

    FIsDestroyed: Boolean;
    FLock: TCriticalSection;
    FSubscribedTopics: TList<string>;
    FCallbacks: TDictionary<string, TProc<string, string>>;
    FPingTask: ITask;
    FSubscribeTask: ITask;

    FTopicsChanged: TEvent;
    FTopicsVersion: Integer;

    FKV: IKeyValueStore;

    procedure InitClient;
    procedure InitSubscriber;

    procedure ReconnectClientOnly;
    procedure StartPingLoop;
    procedure StartSubscribeLoop;

    function IsClientConnected: Boolean;
    class function Jitter(const BaseMs: Integer): Integer; static;
  public
    constructor Create;
    destructor Destroy; override;

    class function GetInstance: TRedis; static;

    procedure Publish(const Topic, Message: string);
    procedure Subscribe(const Topic: string; Callback: TProc<string, string>);

    property KV: IKeyValueStore read FKV;
  end;

var
  SingletonInstance: TRedis;

implementation

const
  CMD_TIMEOUT_MS = 3000;
  PING_INTERVAL_MS = 5000;
  RECONNECT_BASE_MS = 1000;

type
  TRedisKeyValueAdapter = class(TInterfacedObject, IKeyValueStore)
  private
    FOwner: TRedis;
    function EnsureClientOrRetry(const Proc: TProc): Boolean; overload;
    function EnsureClientOrRetry<T>(const Func: TFunc<T>): T; overload;
  public
    constructor Create(AOwner: TRedis);
    function Get(const Key: string): string;
    function SetEX(const Key, Value: string; const TTLSeconds: Integer): Boolean;
    function SetNXEX(const Key, Value: string; const TTLSeconds: Integer): Boolean;
    function Del(const Key: string): Integer;
    function Exists(const Key: string): Boolean;
    function Expire(const Key: string; const TTLSeconds: Integer): Boolean;
  end;

{ TRedisKeyValueAdapter }

constructor TRedisKeyValueAdapter.Create(AOwner: TRedis);
begin
  inherited Create;
  FOwner:= AOwner;
end;

function TRedisKeyValueAdapter.EnsureClientOrRetry(const Proc: TProc): Boolean;
var
  NeedReconnect: Boolean;
begin
  Result:= False;
  NeedReconnect:= False;

  FOwner.FLock.Enter;
  try
    try
      if not Assigned(FOwner.FRedisClient) then
        raise Exception.Create('Redis client not initialized');
      Proc();
      Result:= True;
    except
      NeedReconnect:= True;
    end;
  finally
    FOwner.FLock.Leave;
  end;

  if NeedReconnect and (not FOwner.FIsDestroyed) then
  begin
    FOwner.ReconnectClientOnly;
    FOwner.FLock.Enter;
    try
      if Assigned(FOwner.FRedisClient) then
      begin
        Proc();
        Result:= True;
      end;
    finally
      FOwner.FLock.Leave;
    end;
  end;
end;

function TRedisKeyValueAdapter.EnsureClientOrRetry<T>(const Func: TFunc<T>): T;
var
  NeedReconnect: Boolean;
  HasValue: Boolean;
  Value: T;
begin
  NeedReconnect:= False;
  HasValue:= False;

  FOwner.FLock.Enter;
  try
    try
      if not Assigned(FOwner.FRedisClient) then
        raise Exception.Create('Redis client not initialized');
      Value:= Func();
      HasValue:= True;
    except
      NeedReconnect:= True;
    end;
  finally
    FOwner.FLock.Leave;
  end;

  if NeedReconnect and (not FOwner.FIsDestroyed) then
  begin
    FOwner.ReconnectClientOnly;
    FOwner.FLock.Enter;
    try
      if Assigned(FOwner.FRedisClient) then
      begin
        Value:= Func();
        HasValue:= True;
      end;
    finally
      FOwner.FLock.Leave;
    end;
  end;

  if not HasValue then
    raise Exception.Create('Redis KV operation failed');

  Result:= Value;
end;

function TRedisKeyValueAdapter.Get(const Key: string): string;
begin
  Result:= EnsureClientOrRetry<string>(
    function: string
    begin
      Result:= FOwner.FRedisClient.GET(Key);
    end);
end;

function TRedisKeyValueAdapter.SetEX(const Key, Value: string; const TTLSeconds: Integer): Boolean;
begin
  Result:= EnsureClientOrRetry(
    procedure
    begin
      if not FOwner.FRedisClient.&SET(Key, Value) then
        raise Exception.Create('SET failed');
      if (TTLSeconds > 0) and (not FOwner.FRedisClient.EXPIRE(Key, TTLSeconds)) then
        raise Exception.Create('EXPIRE failed');
    end);
end;

function TRedisKeyValueAdapter.SetNXEX(const Key, Value: string; const TTLSeconds: Integer): Boolean;
begin
  Result:= EnsureClientOrRetry<Boolean>(
    function: Boolean
    begin
      Result:= FOwner.FRedisClient.SETNX(Key, Value);
      if Result and (TTLSeconds > 0) then
        FOwner.FRedisClient.EXPIRE(Key, TTLSeconds);
    end);
end;

function TRedisKeyValueAdapter.Del(const Key: string): Integer;
begin
  Result:= EnsureClientOrRetry<Integer>(
    function: Integer
    begin
      Result:= FOwner.FRedisClient.DEL([Key]);
    end);
end;

function TRedisKeyValueAdapter.Exists(const Key: string): Boolean;
begin
  Result:= EnsureClientOrRetry<Boolean>(
    function: Boolean
    begin
      Result:= FOwner.FRedisClient.EXISTS(Key);
    end);
end;

function TRedisKeyValueAdapter.Expire(const Key: string; const TTLSeconds: Integer): Boolean;
begin
  Result:= EnsureClientOrRetry<Boolean>(
    function: Boolean
    begin
      Result:= FOwner.FRedisClient.EXPIRE(Key, TTLSeconds);
    end);
end;

{ ---------- TRedis ---------- }

constructor TRedis.Create;
begin
  inherited Create;
  Randomize;

  FLock:= TCriticalSection.Create;
  FSubscribedTopics:= TList<string>.Create;
  FCallbacks:= TDictionary<string, TProc<string, string>>.Create;

  FTopicsChanged:= TEvent.Create(nil, True{manualReset}, False{initialState}, '');
  FTopicsVersion:= 0;

  InitClient;
  FKV:= TRedisKeyValueAdapter.Create(Self);

  StartPingLoop;
end;

destructor TRedis.Destroy;
begin
  FIsDestroyed:= True;

  FLock.Enter;
  try
    try
      if Assigned(FRedisSubscriber) then
        FRedisSubscriber.Disconnect;
    except end;
    try
      if Assigned(FRedisClient) then
        FRedisClient.Disconnect;
    except end;
  finally
    FLock.Leave;
  end;

  if Assigned(FSubscribeTask) then
    FSubscribeTask.Wait;
  if Assigned(FPingTask) then
    FPingTask.Wait;

  FSubscribedTopics.Free;
  FCallbacks.Free;
  FTopicsChanged.Free;
  FLock.Free;

  inherited;
end;

class function TRedis.Jitter(const BaseMs: Integer): Integer;
begin
  Result:= BaseMs + Random(250);
end;

procedure TRedis.InitClient;
begin
  FLock.Enter;
  try
    if Assigned(FRedisClient) then
    begin
      try
        FRedisClient.Disconnect;
      except end;
    end;

    FRedisClient:= NewRedisClient(GetRedisHost, GetRedisPort);
    FRedisClient.SetCommandTimeout(CMD_TIMEOUT_MS);
    if GetRedisPassword <> '' then
      FRedisClient.AUTH(GetRedisPassword);
  finally
    FLock.Leave;
  end;
end;

procedure TRedis.InitSubscriber;
begin
  FLock.Enter;
  try
    if Assigned(FRedisSubscriber) then
    begin
      try
        FRedisSubscriber.Disconnect;
      except end;
    end;

    FRedisSubscriber:= NewRedisClient(GetRedisHost, GetRedisPort);
    FRedisSubscriber.SetCommandTimeout(CMD_TIMEOUT_MS);
    if GetRedisPassword <> '' then
      FRedisSubscriber.AUTH(GetRedisPassword);
  finally
    FLock.Leave;
  end;
end;

procedure TRedis.ReconnectClientOnly;
var
  Backoff: Integer;
begin
  Backoff:= RECONNECT_BASE_MS;
  while not FIsDestroyed do
  begin
    try
      InitClient;
      Break;
    except
      TThread.Sleep(Jitter(Backoff));
      if Backoff < 30000 then
        Backoff:= Backoff * 2;
    end;
  end;
end;

function TRedis.IsClientConnected: Boolean;
begin
  FLock.Enter;
  try
    try
      Result:= Assigned(FRedisClient) and (FRedisClient.PING = 'PONG');
    except
      Result:= False;
    end;
  finally
    FLock.Leave;
  end;
end;

procedure TRedis.StartPingLoop;
begin
  FPingTask:= TTask.Run(
    procedure
    begin
      while not FIsDestroyed do
      begin
        if not IsClientConnected then
          ReconnectClientOnly;
        TThread.Sleep(PING_INTERVAL_MS);
      end;
    end);
end;

procedure TRedis.StartSubscribeLoop;
var
  Backoff: Integer;
begin
  if Assigned(FSubscribeTask) then
    Exit;

  Backoff:= RECONNECT_BASE_MS;

  FSubscribeTask:= TTask.Run(
    procedure
    var
      Topics: TArray<string>;
      LocalVersion: Integer;
    begin
      while not FIsDestroyed do
      begin
        FLock.Enter;
        try
          Topics:= FSubscribedTopics.ToArray;
          LocalVersion:= FTopicsVersion;
          FTopicsChanged.ResetEvent;
        finally
          FLock.Leave;
        end;

        if Length(Topics) = 0 then
        begin
          if FIsDestroyed then Exit;
          FTopicsChanged.WaitFor(PING_INTERVAL_MS);
          Continue;
        end;

        try
          InitSubscriber;

          FRedisSubscriber.SUBSCRIBE(
            Topics,
            procedure(Topic, Msg: string)
            begin
              try
                FLock.Enter;
                try
                  if FCallbacks.ContainsKey(Topic) then
                    FCallbacks[Topic](Topic, Msg);
                finally
                  FLock.Leave;
                end;
              except
              end;
            end,
            function: Boolean
            begin
              Result:= not FIsDestroyed;

              if Result then
              begin
                FLock.Enter;
                try
                  if LocalVersion <> FTopicsVersion then
                  begin
                    try
                      if Assigned(FRedisSubscriber) then
                        FRedisSubscriber.Disconnect;
                    except end;
                    Result:= False;
                  end;
                finally
                  FLock.Leave;
                end;
              end;
            end
          );

          Backoff:= RECONNECT_BASE_MS;
        except
          on E: Exception do
          begin
            Log('Subscribe error: ' + E.Message);
            TThread.Sleep(Jitter(Backoff));
            if Backoff < 15000 then
              Backoff:= Backoff * 2;
          end;
        end;
      end;
    end);
end;

procedure TRedis.Publish(const Topic, Message: string);
var
  NeedReconnect: Boolean;
begin
  NeedReconnect:= False;

  FLock.Enter;
  try
    try
      if not Assigned(FRedisClient) then
        raise Exception.Create('Redis client not initialized');
      FRedisClient.PUBLISH(Topic, Message);
    except
      on E: Exception do
      begin
        Log('Publish error: ' + E.Message);
        NeedReconnect:= True;
      end;
    end;
  finally
    FLock.Leave;
  end;

  if NeedReconnect and (not FIsDestroyed) then
  begin
    ReconnectClientOnly;
    FLock.Enter;
    try
      if Assigned(FRedisClient) then
        FRedisClient.PUBLISH(Topic, Message);
    finally
      FLock.Leave;
    end;
  end;
end;

procedure TRedis.Subscribe(const Topic: string; Callback: TProc<string, string>);
var
  ShouldStart: Boolean;
begin
  ShouldStart:= False;

  FLock.Enter;
  try
    if not FSubscribedTopics.Contains(Topic) then
    begin
      FSubscribedTopics.Add(Topic);
      FCallbacks.Add(Topic, Callback);
      Inc(FTopicsVersion);
      FTopicsChanged.SetEvent;

      if FSubscribedTopics.Count = 1 then
        ShouldStart:= True;
    end
    else
    begin
      FCallbacks[Topic]:= Callback;
    end;
  finally
    FLock.Leave;
  end;

  if ShouldStart then
    StartSubscribeLoop;
end;

class function TRedis.GetInstance: TRedis;
begin
  if not Assigned(SingletonInstance) then
    SingletonInstance:= TRedis.Create;
  Result:= SingletonInstance;
end;

initialization
  SingletonInstance:= nil;

finalization
  SingletonInstance.Free;

end.

