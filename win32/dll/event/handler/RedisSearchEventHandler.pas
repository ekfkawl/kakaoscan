unit RedisSearchEventHandler;

interface

uses
  Winapi.Windows, System.Classes, System.SysUtils, LogUtil, RedisConfig, System.Threading, RedisUtil,
  EventMetadata, SearchEvent, SetStatusEvent, GuardObjectUtil;

implementation

var
  Task: ITask;

procedure OnSearchEventReceived(Topic, JsonMessage: string);
var
  SearchEvent: TSearchEvent;
  SetStatusEvent: TSetStatusEvent;
  Redis: TRedis;
begin
  Guard(SearchEvent, TSearchEvent.Create(JsonMessage));

  Guard(SetStatusEvent, TSetStatusEvent.Create(SearchEvent.EventId));
  SetStatusEvent.Status:= EVENT_FAILURE;
  SetStatusEvent.Message:= 'test message';

  Redis:= TRedis.GetInstance;
  Redis.Publish(TOPIC_OTHER_EVENT, SetStatusEvent.ToJSON);
  WriteLn(SetStatusEvent.ToJSON);

  WriteLn(Format('received message on topic %s: %s', [Topic, SearchEvent.EventId]));
end;

initialization
  Task:= TTask.Run(
    procedure
    var
      Redis: TRedis;
    begin
      Redis:= TRedis.GetInstance;
      Redis.Subscribe(TOPIC_SEARCH_EVENT, OnSearchEventReceived);
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
