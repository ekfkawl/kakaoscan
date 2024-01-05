unit MemoryUtil;

interface

uses
  Winapi.Windows;

function GetCallAddress(const Address: DWORD): DWORD;
procedure CallHook(const HookAddress: DWORD; DestAddress: Pointer);
procedure JumpHook(const HookAddress: DWORD; DestAddress: Pointer);
procedure WriteProtectedMemory1(const Address: DWORD; dbValue: Byte);
procedure WriteProtectedMemory2(const Address: DWORD; dwValue: Word);
procedure WriteProtectedMemory4(const Address, ddValue: DWORD);

implementation

function GetCallAddress(const Address: DWORD): DWORD;
begin
  Result:= Integer(Address) + PInteger(Address + 1)^ + 5;
end;

procedure CallHook(const HookAddress: DWORD; DestAddress: Pointer);
var
  dOldProtect: DWORD;
begin
  VirtualProtect(Ptr(HookAddress), 8, PAGE_EXECUTE_READWRITE, dOldProtect);
  PBYTE(HookAddress)^:= $E8;
  PDWORD(HookAddress + 1)^:= DWORD(DestAddress) - HookAddress - 5;
  VirtualProtect(Ptr(HookAddress), 8, dOldProtect, dOldProtect);
end;

procedure JumpHook(const HookAddress: DWORD; DestAddress: Pointer);
var
  dOldProtect: DWORD;
begin
  VirtualProtect(Ptr(HookAddress), 8, PAGE_EXECUTE_READWRITE, dOldProtect);
  PBYTE(HookAddress)^:= $E9;
  PDWORD(HookAddress + 1)^:= DWORD(DestAddress) - HookAddress - 5;
  VirtualProtect(Ptr(HookAddress), 8, dOldProtect, dOldProtect);
end;

procedure WriteProtectedMemory1(const Address: DWORD; dbValue: Byte);
var
  dOldProtect: DWORD;
begin
  VirtualProtect(ptr(Address), 4, PAGE_EXECUTE_READWRITE, dOldProtect);
  PBYTE(Address)^:= dbValue;
  VirtualProtect(ptr(Address), 4, dOldProtect, dOldProtect);
end;

procedure WriteProtectedMemory2(const Address: DWORD; dwValue: Word);
var
  dOldProtect: DWORD;
begin
  VirtualProtect(ptr(Address), 4, PAGE_EXECUTE_READWRITE, dOldProtect);
  PWORD(Address)^:= dwValue;
  VirtualProtect(ptr(Address), 4, dOldProtect, dOldProtect);
end;

procedure WriteProtectedMemory4(const Address, ddValue: DWORD);
var
  dOldProtect: DWORD;
begin
  VirtualProtect(ptr(Address), 4, PAGE_EXECUTE_READWRITE, dOldProtect);
  PDWORD(Address)^:= ddValue;
  VirtualProtect(ptr(Address), 4, dOldProtect, dOldProtect);
end;

end.
