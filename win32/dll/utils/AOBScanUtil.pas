unit AOBScanUtil;

interface

uses
  Winapi.Windows, System.SysUtils, System.Classes, System.StrUtils, System.Types;

type
  TScanStructure = record
    hProcess: THandle;
    Value: string;
    StartAddr: DWORD;
    EndAddr: DWORD;
  end;

  TAOBScanner = class
  private
    FScanStructure: TScanStructure;
    constructor Create;
    function InitializePattern(const Value: string): TByteDynArray;
    function ReadMemory(const BaseAddress: Pointer; Size: SIZE_T): TBytes;
    function IsPatternMatch(const Buffer: TBytes; const Pattern: TByteDynArray): boolean;
  public
    class function GetInstance: TAOBScanner;
    function AOBSCAN: TStringList; overload;
    function AOBSCAN(dwStart, dwEnd: DWORD; Val: string; Index: Integer = 0): DWORD; overload;
    function AOBSCAN(Val: string; Index: Integer = 0): DWORD; overload;
    function AOBSCAN(Val: string; Index: Integer; CallbackProc: TProc<DWORD>): DWORD; overload;
    procedure UpdateScanStructure(const AStartAddr, AEndAddr: DWORD);
  end;

implementation

var
  SingletonInstance: TAOBScanner;

{ TAOBScanner }

constructor TAOBScanner.Create;
begin
  inherited Create;
  FScanStructure.hProcess:= GetCurrentProcess;
end;

class function TAOBScanner.GetInstance: TAOBScanner;
begin
  if not Assigned(SingletonInstance) then
    SingletonInstance:= TAOBScanner.Create;
  Result:= SingletonInstance;
end;

function TAOBScanner.InitializePattern(const Value: string): TByteDynArray;
var
  ArrStr: TStringDynArray;
begin
  ArrStr:= SplitString(Trim(Value), ' ');
  SetLength(Result, Length(ArrStr));
  for var i:= 0 to High(ArrStr) do
    if ArrStr[i] <> '??' then
      Result[i]:= ('0x' + ArrStr[i]).ToInteger
    else
      Result[i]:= 0;
end;

function TAOBScanner.ReadMemory(const BaseAddress: Pointer; Size: SIZE_T): TBytes;
begin
  SetLength(Result, Size);
  if not ReadProcessMemory(FScanStructure.hProcess, BaseAddress, Pointer(Result), Size, PSIZE_T(nil)^) then
    SetLength(Result, 0);
end;

function TAOBScanner.IsPatternMatch(const Buffer: TBytes; const Pattern: TByteDynArray): boolean;
var
  i: Integer;
begin
  Result:= False;
  if Length(Buffer) < Length(Pattern) then
    Exit;

  for i:= 0 to High(Pattern) do
    if (Pattern[i] <> 0) and (Buffer[i] <> Pattern[i]) then
      Exit;

  Result:= True;
end;

function TAOBScanner.AOBSCAN: TStringList;
var
  ScanAddr: Pointer;
  mbi: MEMORY_BASIC_INFORMATION;
  Pattern, Buffer: TBytes;
  BaseAddress: NativeUInt;
  i: NativeUInt;
begin
  Result:= TStringList.Create;
  Pattern:= InitializePattern(FScanStructure.Value);
  ScanAddr:= Ptr(FScanStructure.StartAddr);

  while NativeUInt(ScanAddr) <= FScanStructure.EndAddr do
  begin
    VirtualQueryEx(FScanStructure.hProcess, ScanAddr, mbi, SizeOf(mbi));
    if (mbi.RegionSize > 0) and (mbi.State = MEM_COMMIT) then
    begin
      Buffer:= ReadMemory(mbi.BaseAddress, mbi.RegionSize);
      BaseAddress:= NativeUInt(mbi.BaseAddress);
      if Length(Buffer) > 0 then
        for i:= 0 to High(Buffer) - Length(Pattern) + 1 do
          if IsPatternMatch(Copy(Buffer, i, Length(Pattern)), Pattern) then
            Result.Add((BaseAddress + i).ToHexString);
    end;
    ScanAddr:= Ptr(NativeUInt(mbi.BaseAddress) + mbi.RegionSize);
  end;
end;

function TAOBScanner.AOBSCAN(dwStart, dwEnd: DWORD; Val: string; Index: Integer = 0): DWORD;
var
  Results: TStringList;
begin
  Results:= AOBSCAN;
  try
    if (Index < Results.Count) then
      Result:= ('0x' + Results[Index]).ToInteger
    else
      Result:= 0;
  finally
    Results.Free;
  end;
end;

function TAOBScanner.AOBSCAN(Val: string; Index: Integer = 0): DWORD;
begin
  FScanStructure.Value:= Val;
  Result:= AOBSCAN(FScanStructure.StartAddr, FScanStructure.EndAddr, Val, Index);
end;

function TAOBScanner.AOBSCAN(Val: string; Index: Integer; CallbackProc: TProc<DWORD>): DWORD;
begin
  FScanStructure.Value:= Val;
  Result:= AOBSCAN(FScanStructure.StartAddr, FScanStructure.EndAddr, Val, Index);
  if (Result > 0) and (Assigned(CallbackProc)) then
    CallbackProc(Result);
end;

procedure TAOBScanner.UpdateScanStructure(const AStartAddr, AEndAddr: DWORD);
begin
  FScanStructure.StartAddr:= AStartAddr;
  FScanStructure.EndAddr:= AEndAddr;
end;

initialization
  SingletonInstance:= nil;

finalization
  SingletonInstance.Free;

end.
