unit EventMetadata;

interface

uses
  Winapi.Windows, System.Classes;

type
  TEventMetadata = class
  private
    FEventId: string;
    FCreatedAt: TDateTime;
  public
    property EventId: string read FEventId write FEventId;
    property CreatedAt: TDateTime read FCreatedAt write FCreatedAt;
    constructor Create(const EventId: string); overload;
  end;

implementation

{ TEventMetadata }

constructor TEventMetadata.Create(const EventId: string);
begin
  inherited Create;
  FEventId:= EventId;
end;

end.
