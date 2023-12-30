unit EventStatus;

interface

uses
  Winapi.Windows, System.Classes, EventMetadata, REST.Json, System.SysUtils;

type
  TEventStatus = class
  private
    FStatus: string;
    FMessage: string;
  public
    function ToJSON: string;
    property Status: string read FStatus write FStatus;
    property Message: string read FMessage write FMessage;

    class function FromJSON(const AJson: string): TEventStatus;
    class function CreateInstance(const Status, Message: string): TEventStatus;
  end;

implementation

{ TEventStatus }

function TEventStatus.ToJSON: string;
begin
  Result:= TJson.ObjectToJsonString(Self);
end;

class function TEventStatus.FromJSON(const AJson: string): TEventStatus;
begin
  Result:= TJson.JsonToObject<TEventStatus>(AJson);
end;

class function TEventStatus.CreateInstance(const Status, Message: string): TEventStatus;
begin
  Result:= TEventStatus.Create;
  Result.FStatus:= Status;
  Result.FMessage:= Message;
end;

end.
