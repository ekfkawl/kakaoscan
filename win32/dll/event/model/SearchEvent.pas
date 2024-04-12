unit SearchEvent;

interface

uses
  Winapi.Windows, System.Classes, EventMetadata, REST.Json, System.JSON, System.SysUtils;

type
  TSearchEvent = class(TEventMetadata)
  private
    FEmail: string;
    FPhoneNumber: string;
  public
    property Email: string read FEmail write FEmail;
    property PhoneNumber: string read FPhoneNumber write FPhoneNumber;
    constructor Create; overload;
    constructor Create(const JSONString: string); overload;
    function ToJSON: string;
    function ToEventJSON: string;
  end;

implementation

{ TSearchEvent }

constructor TSearchEvent.Create;
begin
  inherited Create;
end;

constructor TSearchEvent.Create(const JSONString: string);
var
  JSONValue: TJSONValue;
  DataObject: TJSONObject;
begin
  inherited Create;

  JSONValue:= TJSONObject.ParseJSONValue(JSONString);
  try
    if JSONValue.TryGetValue<TJSONObject>('data', DataObject) then
    begin
      Self:= TJson.JsonToObject<TSearchEvent>(DataObject.ToJSON);
    end;
  finally
    JSONValue.Free;
  end;
end;

function TSearchEvent.ToJSON: string;
begin
  Result:= TJson.ObjectToJsonString(Self);
end;

function TSearchEvent.ToEventJSON: string;
begin
  Result:= Format('{"eventType":"SearchEvent","data":%s}', [Self.ToJSON]);
end;


end.
