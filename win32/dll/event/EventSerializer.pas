unit EventSerializer;

interface

uses
  System.SysUtils, System.JSON, EventMetadata;

type
  TEventSerializer = class
  public
    class function Serialize(AEvent: TEventMetadata): string; static;
  end;


implementation

{ TEventSerializer }



end.
