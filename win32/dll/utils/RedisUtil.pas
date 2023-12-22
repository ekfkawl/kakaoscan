unit RedisUtil;

interface

uses
  Redis.Client, Redis.Commons, Redis.NetLib.INDY, System.SysUtils, LogUtil, RedisConfig;

type
  TRedis = class
  private
    FRedisClient: IRedisClient;
    FRedisSubscriber: IRedisClient;
    constructor Create;
  public
    destructor Destroy; override;

    procedure Publish(const Topic, Message: string);
    procedure Subscribe(const Topic: string; Callback: TProc<string, string>);

    class function GetInstance: TRedis;
  end;

var
  SingletonInstance: TRedis;

implementation

{ TRedis }

constructor TRedis.Create;
begin
  inherited Create;
  FRedisClient:= NewRedisClient(GetRedisHost, 6379);
  FRedisSubscriber:= NewRedisClient(GetRedisHost, 6379);
end;

destructor TRedis.Destroy;
begin
  FRedisClient:= nil;
  FRedisSubscriber:= nil;
  inherited;
end;

procedure TRedis.Publish(const Topic, Message: string);
begin
  try
    FRedisClient.PUBLISH(Topic, Message);
  except
    on E: Exception do
      Log('redis publish error', E);
  end;
end;

procedure TRedis.Subscribe(const Topic: string; Callback: TProc<string, string>);
begin
  FRedisSubscriber.SUBSCRIBE([Topic],
    procedure(Topic, JsonMessage: string)
    begin
      Callback(Topic, JsonMessage);
    end,
    function: Boolean
    begin
      Result:= FRedisSubscriber <> nil;
    end,
    nil
  );
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
