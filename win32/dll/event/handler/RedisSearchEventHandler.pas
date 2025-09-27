unit RedisSearchEventHandler;

interface

uses
  Winapi.Windows, System.Classes, System.SysUtils, LogUtils, System.Threading,
  Main, EventMetadata, SearchEvent, EventStatus, GuardObjectUtils, Redis.Config, RedisUtils;

implementation

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
  TRedis.GetInstance.Subscribe(SEARCH_EVENT_TOPIC, OnSearchEventReceived);

finalization

end.
