unit SearchNewPhoneNumberEvent;

interface

uses
  Winapi.Windows, System.Classes, System.SysUtils, EventMetadata, REST.Json, System.JSON, SearchEvent;

type
  TSearchNewPhoneNumberEvent = class(TSearchEvent)
  private
  public
    constructor Create; overload;
    constructor Create(const Email, PhoneNumber: string); overload;
    function ToJSON: string;
    function ToEventJSON: string;
  end;

implementation

{ TSearchNewPhoneNumberEvent }

constructor TSearchNewPhoneNumberEvent.Create;
begin
  inherited Create;
end;

constructor TSearchNewPhoneNumberEvent.Create(const Email, PhoneNumber: string);
begin
  inherited Create('', Email, PhoneNumber);
end;

function TSearchNewPhoneNumberEvent.ToJSON: string;
begin
  Result:= TJson.ObjectToJsonString(Self);
end;

function TSearchNewPhoneNumberEvent.ToEventJSON: string;
begin
  Result:= Format('{"eventType":"SearchNewPhoneNumberEvent","data":%s}', [Self.ToJSON]);
end;

end.
