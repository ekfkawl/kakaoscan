unit GuardObjectUtils;

interface

function Guard(out Reference; Instance: TObject): IUnknown;

implementation

type
  TGuardObject = class(TInterfacedObject)
private
  FInstance: TObject;
public
  constructor Create(Instance: TObject);
  destructor Destroy; override;
end;

{ TGuardObject }

constructor TGuardObject.Create(Instance: TObject);
begin
  FInstance:= Instance;
end;

destructor TGuardObject.Destroy;
begin
  FInstance.Free;
  inherited;
end;

function Guard(out Reference; Instance: TObject): IUnknown;
begin
  Result:= TGuardObject.Create(Instance);
  TObject(Reference):= Instance;
end;

end.
