unit SetStatusEvent;

interface

uses
  Winapi.Windows, System.Classes, EventMetadata, REST.Json, System.SysUtils;

type
  TSetStatusEvent = class(TEventMetadata)
  private
    FStatus: string;
    FMessage: string;
  public
    constructor Create(EventId: string);
    function ToJSON: string;
    property Status: string read FStatus write FStatus;
    property Message: string read FMessage write FMessage;
  end;

implementation

{ TSetStatusEvent }

constructor TSetStatusEvent.Create(EventId: string);
var
  GUID: TGUID;
begin
  inherited Create;
  CreateGUID(GUID);
  Self.EventId:= EventId;
  Self.CreatedAt:= Now;
end;

function TSetStatusEvent.ToJSON: string;
begin
  Result:= Format('{"eventType": "SetStatusEvent","data": %s}', [TJson.ObjectToJsonString(Self)]);
end;

end.
