unit RedisSearchEventHandler;

interface

uses
  Winapi.Windows, System.Classes, System.SysUtils, LogUtil, RedisConfig, System.Threading, RedisUtil,
  EventMetadata, SearchEvent, EventStatus, GuardObjectUtil;

implementation

var
  Task: ITask;

procedure OnSearchEventReceived(Topic, JsonMessage: string);
var
  Redis: TRedis;
  SearchEvent: TSearchEvent;
begin
  try
    Redis:= TRedis.GetInstance;

    Guard(SearchEvent, TSearchEvent.Create(JsonMessage));
    Redis.SetEventStatus(SearchEvent.EventId, TEventStatus.CreateInstance(EVENT_SUCCESS, '¼º°ø' + Random(4124).ToString));

    WriteLn(Format('received message on topic %s: %s', [Topic, SearchEvent.EventId]));
  except
    on E: Exception do
      Log('handle OnSearchEventReceived error', E);
  end;
end;

initialization
  Task:= TTask.Run(
    procedure
    var
      Redis: TRedis;
    begin
      Redis:= TRedis.GetInstance;
      while True do
      begin
        if Redis.IsConnected then
        begin
          Redis.Subscribe(TOPIC_SEARCH_EVENT, OnSearchEventReceived);
        end else
        begin
          Sleep(1000);
          Redis.Reconnect;
        end;
      end;
    end
  );

finalization
  if Assigned(Task) and
     (Task.Status <> TTaskStatus.Completed) and
     (Task.Status <> TTaskStatus.Canceled) and
     (Task.Status <> TTaskStatus.Exception) then
  begin
    Task.Cancel;
    Task.Wait(100);
  end;
  Task:= nil;

end.
