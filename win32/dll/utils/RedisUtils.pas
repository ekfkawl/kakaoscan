unit RedisUtils;

interface

uses
  Winapi.Windows, System.Classes, System.SysUtils, System.SyncObjs, System.Generics.Collections, System.Threading, System.JSON, REST.Json, DateUtils,
  Redis.Client, Redis.Commons, Redis.NetLib.INDY, Redis.Config;

type
  IKeyValueStore = interface
    ['{00D3C654-F40A-4642-A3BD-1254EFD5E51C}']
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
    FCmdLock: TCriticalSection;
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
    class function UnixNowSeconds: Double; static;
  public
    constructor Create;
    destructor Destroy; override;

    class function GetInstance: TRedis; static;

    procedure Publish(const Topic, Message: string);
    procedure Subscribe(const Topic: string; Callback: TProc<string, string>);

    property KV: IKeyValueStore read FKV;

    procedure PublishServer(const Topic, Command: string; BodyValue: TJSONValue); overload;
    procedure PublishServer(const Topic, Command, BodyJson: string); overload;
    procedure PublishServer(const Topic, Command: string; BodyObject: TObject); overload;

  end;

implementation

var
  SingletonInstance: TRedis;

const
  CMD_TIMEOUT_MS = 3000;
  SUB_TIMEOUT_MS  = 60000;

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

  FOwner.FCmdLock.Enter;
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
    FOwner.FCmdLock.Leave;
  end;

  if NeedReconnect and (not FOwner.FIsDestroyed) then
  begin
    FOwner.ReconnectClientOnly;

    FOwner.FCmdLock.Enter;
    try
      if Assigned(FOwner.FRedisClient) then
      begin
        Proc();
        Result:= True;
      end;
    finally
      FOwner.FCmdLock.Leave;
    end;
  end;
end;

function TRedisKeyValueAdapter.EnsureClientOrRetry<T>(const Func: TFunc<T>): T;
var
  NeedReconnect, HasValue: Boolean;
  Value: T;
begin
  NeedReconnect:= False; HasValue:= False;

  FOwner.FCmdLock.Enter;
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
    FOwner.FCmdLock.Leave;
  end;

  if NeedReconnect and (not FOwner.FIsDestroyed) then
  begin
    FOwner.ReconnectClientOnly;

    FOwner.FCmdLock.Enter;
    try
      if Assigned(FOwner.FRedisClient) then
      begin
        Value:= Func();
        HasValue:= True;
      end;
    finally
      FOwner.FCmdLock.Leave;
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
  FCmdLock:= TCriticalSection.Create;
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

  // 구독/클라이언트 소켓 종료
  try if Assigned(FRedisSubscriber) then FRedisSubscriber.Disconnect; except end;

  FCmdLock.Enter;
  try
    try if Assigned(FRedisClient) then FRedisClient.Disconnect; except end;
  finally
    FCmdLock.Leave;
  end;

  // 태스크 종료 대기
  if Assigned(FSubscribeTask) then FSubscribeTask.Wait;
  if Assigned(FPingTask)      then FPingTask.Wait;

  // 상태 자료구조 정리
  FSubscribedTopics.Free;
  FCallbacks.Free;
  FTopicsChanged.Free;

  // 락 해제
  FCmdLock.Free;
  FLock.Free;

  inherited;
end;

class function TRedis.Jitter(const BaseMs: Integer): Integer;
begin
  Result:= BaseMs + Random(250);
end;

procedure TRedis.InitClient;
begin
  FCmdLock.Enter;
  try
    if Assigned(FRedisClient) then
      try FRedisClient.Disconnect; except end;

    FRedisClient:= NewRedisClient(GetRedisHost, GetRedisPort);
    FRedisClient.SetCommandTimeout(CMD_TIMEOUT_MS);
    if GetRedisPassword <> '' then
      FRedisClient.AUTH(GetRedisPassword);
  finally
    FCmdLock.Leave;
  end;
end;

procedure TRedis.InitSubscriber;
begin
  if Assigned(FRedisSubscriber) then
  begin
    try
      FRedisSubscriber.Disconnect;
    except end;
  end;

  FRedisSubscriber:= NewRedisClient(GetRedisHost, GetRedisPort);
  FRedisSubscriber.SetCommandTimeout(SUB_TIMEOUT_MS);
  if GetRedisPassword <> '' then
    FRedisSubscriber.AUTH(GetRedisPassword);
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
  FCmdLock.Enter;
  try
    try
      Result:= Assigned(FRedisClient) and (FRedisClient.PING = 'PONG');
    except
      Result:= False;
    end;
  finally
    FCmdLock.Leave;
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
const
  MAX_IDLE_TIMEOUT_MS = 10 * 60 * 1000;
begin
  if Assigned(FSubscribeTask) then
    Exit;

  Backoff:= RECONNECT_BASE_MS;

  FSubscribeTask:= TTask.Run(
    procedure
    var
      Topics: TArray<string>;
      LocalVersion: Integer;
      LastActiveTick: Int64;
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

          LastActiveTick:= GetTickCount64;

          FRedisSubscriber.SUBSCRIBE(
            Topics,
            // 메시지 수신 시
            procedure(Topic, Msg: string)
            var
              cb: TProc<string, string>;
            begin
              LastActiveTick:= GetTickCount64;

              cb:= nil;
              FLock.Enter;
              try
                FCallbacks.TryGetValue(Topic, cb);
              finally
                FLock.Leave;
              end;

              if Assigned(cb) then
              begin
                try
                  cb(Topic, Msg);
                except
                end;
              end;
            end,

            function: Boolean
            var
              needReconnect: Boolean;
              CurrentTick: Int64;
            begin
              Result:= not FIsDestroyed;
              needReconnect:= False;
              CurrentTick:= GetTickCount64;

              if Result then
              begin
                FLock.Enter;
                try
                  // 토픽 목록이 변경되었으면 재구독
                  if LocalVersion <> FTopicsVersion then
                  begin
                    Result:= False;
                    needReconnect:= True;
                  end;
                finally
                  FLock.Leave;
                end;

                if Result and ((CurrentTick - LastActiveTick) > MAX_IDLE_TIMEOUT_MS) then
                begin
                  Result:= False;
                  needReconnect:= True;
                end;
              end;

              if needReconnect then
              begin
                try
                  if Assigned(FRedisSubscriber) then
                    FRedisSubscriber.Disconnect;
                except
                end;
              end;
            end
          );

          Backoff:= RECONNECT_BASE_MS;

        except
          on E: Exception do
          begin
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

  FCmdLock.Enter;
  try
    try
      if not Assigned(FRedisClient) then
        raise Exception.Create('Redis client not initialized');
      FRedisClient.PUBLISH(Topic, Message);
    except
      on E: Exception do
      begin
        NeedReconnect:= True;
      end;
    end;
  finally
    FCmdLock.Leave;
  end;

  if NeedReconnect and (not FIsDestroyed) then
  begin
    ReconnectClientOnly;

    FCmdLock.Enter;
    try
      if Assigned(FRedisClient) then
        FRedisClient.PUBLISH(Topic, Message);
    finally
      FCmdLock.Leave;
    end;
  end;
end;

class function TRedis.UnixNowSeconds: Double;
var
  DT: TDateTime;
  Sec: Int64;
  Ms : Word;
begin
  DT:= Now;
  Sec:= DateTimeToUnix(DT, False);
  Ms := MilliSecondOf(DT);
  Result:= Sec + (Ms / 1000.0);
end;

procedure TRedis.PublishServer(const Topic, Command: string; BodyValue: TJSONValue);
var
  Root : TJSONObject;
  BodyC: TJSONValue;
  Msg  : string;
begin
  if Assigned(BodyValue) then
    BodyC:= BodyValue.Clone as TJSONValue
  else
    BodyC:= TJSONObject.Create;

  Root:= TJSONObject.Create;
  try
    Root.AddPair('command', Command);
    Root.AddPair('body', BodyC);
    Root.AddPair('issuedAt', TJSONNumber.Create(UnixNowSeconds));

    Msg:= Root.ToJSON;
  finally
    Root.Free;
  end;

  Publish(Topic, Msg);
end;

procedure TRedis.PublishServer(const Topic, Command, BodyJson: string);
var
  V: TJSONValue;
begin
  V:= TJSONObject.ParseJSONValue(BodyJson);
  try
    if Assigned(V) then
      PublishServer(Topic, Command, V)
    else
      PublishServer(Topic, Command, TJSONString.Create(BodyJson));
  finally
    V.Free;
  end;
end;

procedure TRedis.PublishServer(const Topic, Command: string; BodyObject: TObject);
var
  V: TJSONValue;
begin
  if Assigned(BodyObject) then
    V:= TJson.ObjectToJsonObject(BodyObject)
  else
    V:= TJSONObject.Create;

  try
    PublishServer(Topic, Command, V);
  finally
    V.Free;
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
      Inc(FTopicsVersion);
      FTopicsChanged.SetEvent;
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

