unit InvalidPhoneNumber;

interface

uses
  Winapi.Windows, System.Classes, System.SysUtils, REST.Json;

type
  TInvalidPhoneNumber = class
  private
    FEmail: string;
    FCreatedAt: TDateTime;
  public
    function ToJSON: string;
    property Email: string read FEmail write FEmail;
    property CreatedAt: TDateTime read FCreatedAt write FCreatedAt;

    class function CreateInstance(const Email: string): TInvalidPhoneNumber;
  end;

implementation

class function TInvalidPhoneNumber.CreateInstance(const Email: string): TInvalidPhoneNumber;
begin
  Result:= TInvalidPhoneNumber.Create;
  Result.FEmail:= Email;
  Result.FCreatedAt:= Now;
end;

function TInvalidPhoneNumber.ToJSON: string;
begin
  Result:= TJson.ObjectToJsonString(Self);
end;

end.
