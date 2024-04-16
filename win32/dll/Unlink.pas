unit Unlink;

interface

uses
  Windows;

  procedure HideModule(hModule: HMODULE);

implementation

type
 _UNICODE_STRING = record
  Length: SHORT;
  MaximumLength: SHORT;
  Buffer: PChar;
 end;

  PLDR_MODULE = ^_LDR_MODULE;
  _LDR_MODULE = record
  InLoadOrderModuleList: LIST_ENTRY;
  InMemoryOrderModuleList: LIST_ENTRY;
  InInitializationOrderModuleList: LIST_ENTRY;
  BaseAddress: Pointer;
  EntryPoint: Pointer;
  SizeOfImage: ULONG;
  LibraryFullName: _UNICODE_STRING;
  LibraryBaseName: _UNICODE_STRING;
  Flags: ULONG;
  LoadCount: SHORT;
  TlsIndex: SHORT;
  HashTableEntry: LIST_ENTRY;
  TimeDateStamp: ULONG;
 end;

 PPEB_LDR_DATA = ^_PEB_LDR_DATA;
 _PEB_LDR_DATA = record
  Length: ULONG;
  Initialized: UCHAR;
  SsHandle: Pointer;
  InLoadOrderModuleList: LIST_ENTRY;
  InMemoryOrderModuleList: LIST_ENTRY;
  InInitializationOrderModuleList: LIST_ENTRY;
 end;

 procedure HideModule(hModule: HMODULE);
   function GetLdr:Pointer; stdcall;
   asm
    {$IFDEF CPUX86}
    xor eax, eax
    mov eax, fs:[eax+$18]
    mov eax, [eax+$30]
    mov eax, [eax+$0C]
    {$ELSE}
    xor rax, rax
    mov rax, gs:[rax+$30]
    mov rax, [rax+$60]
    mov rax, [rax+$18]
    {$ENDIF}
   end;
   procedure Unlink(var LISTENTRY: LIST_ENTRY);
   begin
    LISTENTRY.Blink.Flink:= LISTENTRY.Flink;
    LISTENTRY.Flink.Blink:= LISTENTRY.Blink;
   end;
var
  pLdr: PPEB_LDR_DATA;
  pLdrModule: PLDR_MODULE;
begin
  pLdr:= GetLdr;
  pLdrModule:= PLDR_MODULE(pLdr.InLoadOrderModuleList.Flink);
  while ((pLdrModule.BaseAddress <> nil) And (pLdrModule.BaseAddress <> Pointer(hModule))) do
    pLdrModule:= PLDR_MODULE(pLdrModule.InLoadOrderModuleList.Flink);

  if pLdrModule.BaseAddress = nil then
    Exit;

  Unlink(pLdrModule.InLoadOrderModuleList);
  Unlink(pLdrModule.InInitializationOrderModuleList);
  Unlink(pLdrModule.InMemoryOrderModuleList);
  Unlink(pLdrModule.HashTableEntry);
end;

end.
