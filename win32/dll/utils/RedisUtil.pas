unit RedisUtil;

interface

uses
  Redis.Client, Redis.Commons, Redis.NetLib.INDY, System.SysUtils, LogUtil, RedisConfig, EventStatus, InvalidPhoneNumber;

const
  EVENT_KEY_PREFIX = 'eventStatus:';
  INVALID_PHONE_NUMBER_KEY_PREFIX = 'invalidPhoneNumber:';

  EVENT_WAITING = 'WAITING';
  EVENT_PROCESSING = 'PROCESSING';
  EVENT_SUCCESS = 'SUCCESS';
  EVENT_FAILURE = 'FAILURE';

type
  TRedis = class
  private
    FRedisClient: IRedisClient;
    FRedisSubscriber: IRedisClient;
    FIsDestroyed: boolean;
    constructor Create;
  public
    destructor Destroy; override;
    procedure Reconnect;
    procedure Publish(const Topic, Message: string);
    procedure Subscribe(const Topic: string; Callback: TProc<string, string>);
    function SetEventStatus(const EventId: string; Status: TEventStatus): boolean;
    function GetEventStatus(const EventId: string): TEventStatus;
    function CacheInvalidPhoneNumber(const PhoneNumber: string; InvalidPhoneNumber: TInvalidPhoneNumber): boolean;
    function IsConnected: boolean;
    function IsDestroyed: boolean;

    class function GetInstance: TRedis;
  end;

var
  SingletonInstance: TRedis;

implementation

{ TRedis }

constructor TRedis.Create;
begin
  inherited Create;
  FRedisClient:= NewRedisClient(GetRedisHost, GetRedisPort);
  FRedisClient.AUTH(GetRedisPassword);
  Log('RedisClient.PING: '+ FRedisClient.PING);

  FRedisSubscriber:= NewRedisClient(GetRedisHost, GetRedisPort);
  FRedisSubscriber.AUTH(GetRedisPassword);
  Log('RedisSubscriber.PING: '+ FRedisClient.PING);

  FIsDestroyed:= False;
  Reconnect;
end;

destructor TRedis.Destroy;
begin
  if Assigned(FRedisClient) then
  begin
    FRedisClient.Disconnect;
    FRedisClient:= nil;
  end;

  if Assigned(FRedisSubscriber) then
  begin
    FRedisSubscriber.Disconnect;
    FRedisSubscriber:= nil;
  end;

  FIsDestroyed:= True;
  inherited;
end;

procedure TRedis.Reconnect;
begin
  try
    if IsConnected then
      Exit;

    FRedisClient:= nil;
    FRedisSubscriber:= nil;

    FRedisClient:= NewRedisClient(GetRedisHost, GetRedisPort);
    FRedisClient.AUTH(GetRedisPassword);
    FRedisSubscriber:= NewRedisClient(GetRedisHost, GetRedisPort);
    FRedisSubscriber.AUTH(GetRedisPassword);
    Log('redis reconnected');
  except
    on E: Exception do
      Log('redis reconnection error', E);
  end;
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
      Result:= (not FIsDestroyed) and (FRedisSubscriber <> nil);
    end,
    nil
  );
end;

function TRedis.SetEventStatus(const EventId: string; Status: TEventStatus): boolean;
begin
  Result:= False;
  try
    Result:= FRedisClient.&SET(EVENT_KEY_PREFIX + EventId, Status.ToJSON, 600);
    FreeAndNil(Status);
  except
    on E: Exception do
    begin
      Log('redis set event error', E);
    end;
  end;
end;

function TRedis.GetEventStatus(const EventId: string): TEventStatus;
var
  JsonString: string;
begin
  Result:= nil;
  try
    JsonString:= FRedisClient.GET(EVENT_KEY_PREFIX + EventId);
    if JsonString <> '' then
      Result:= TEventStatus.FromJSON(JsonString);
  except
    on E: Exception do
    begin
      Log(Format('redis get fail (eventId: %s)', [EventId]), E);
    end;
  end;
end;

function TRedis.CacheInvalidPhoneNumber(const PhoneNumber: string; InvalidPhoneNumber: TInvalidPhoneNumber): boolean;
begin
  Result:= False;
  try
    Result:= FRedisClient.&SET(INVALID_PHONE_NUMBER_KEY_PREFIX + PhoneNumber, InvalidPhoneNumber.ToJSON, 3600 * 12);
    FreeAndNil(InvalidPhoneNumber);
  except
    on E: Exception do
    begin
      Log('redis cache invalid phonenumber error', E);
    end;
  end;
end;

class function TRedis.GetInstance: TRedis;
begin
  if not Assigned(SingletonInstance) then
    SingletonInstance:= TRedis.Create;
  Result:= SingletonInstance;
end;

function TRedis.IsConnected: boolean;
begin
  Result:= False;
  try
    if Assigned(FRedisClient) and Assigned(FRedisSubscriber) then
     Result:= (FRedisClient.PING = 'PONG') And (FRedisSubscriber.PING = 'PONG');
  except
    on E: Exception do
    begin
      Log('redis connected fail', E);
    end;
  end;
end;

function TRedis.IsDestroyed: boolean;
begin
  Result:= FIsDestroyed;
end;

initialization
  SingletonInstance:= nil;

finalization
  SingletonInstance.Free;

end.
