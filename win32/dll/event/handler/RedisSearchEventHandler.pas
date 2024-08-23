unit RedisSearchEventHandler;

interface

uses
  Winapi.Windows, System.Classes, System.SysUtils, LogUtil, System.Threading,
  Main, EventMetadata, SearchEvent, EventStatus, GuardObjectUtil, RedisConfig, RedisUtil;

implementation

var
  Task: ITask;

procedure OnSearchEventReceived(Topic, JsonMessage: string);
var
  SearchEvent: TSearchEvent;
begin
  try
    Guard(SearchEvent, TSearchEvent.Create(JsonMessage));
    Main.RunEvent(SearchEvent.EventId, SearchEvent.Email, SearchEvent.PhoneNumber, SearchEvent.IsId);
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
      while not Redis.IsDestroyed do
      begin
        if Redis.IsConnected then
        begin
          Redis.Subscribe(SEARCH_EVENT_TOPIC, OnSearchEventReceived);
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
