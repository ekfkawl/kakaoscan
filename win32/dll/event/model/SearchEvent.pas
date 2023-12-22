unit SearchEvent;

interface

uses
  Winapi.Windows, System.Classes, EventMetadata, REST.Json, System.JSON;

type
  TSearchEvent = class(TEventMetadata)
  private
    FEmail: string;
    FPhoneNumber: string;
    FRemoteAddress: string;
  public
    constructor Create; overload;
    constructor Create(const JSONString: string); overload;
    property Email: string read FEmail write FEmail;
    property PhoneNumber: string read FPhoneNumber write FPhoneNumber;
    property RemoteAddress: string read FRemoteAddress write FRemoteAddress;
  end;

implementation

{ TSearchEvent }

constructor TSearchEvent.Create;
begin
  inherited Create;
end;

constructor TSearchEvent.Create(const JSONString: string);
var
  JSONValue, DataValue: TJSONValue;
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

end.
