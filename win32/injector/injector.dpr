program injector;

{$APPTYPE CONSOLE}

{$R *.res}

uses
  System.SysUtils,
  System.Classes,
  Windows;

type TInjectThread = class(TThread)
private
protected
  procedure Execute; override;
public
  constructor Create;
end;

procedure InjectDLL(hProcess: DWORD; DllPath: String);
var
  pRemoteBuffer: Pointer;
  ThreadId: DWORD;
  hThread: THandle;
begin
  pRemoteBuffer:= VirtualAllocEx(hProcess, nil, Length(DllPath), MEM_COMMIT or MEM_RESERVE, PAGE_EXECUTE_READWRITE);
  if (pRemoteBuffer <> nil) And (WriteProcessMemory(hProcess, pRemoteBuffer, @DllPath[1], Length(DllPath) * 2, PSIZE_T(nil)^)) then
  begin
    hThread:= CreateRemoteThread(hProcess, nil, 0, GetProcAddress(LoadLibrary('kernel32.dll'), 'LoadLibraryW'), pRemoteBuffer, 0, ThreadId);
    WaitForSingleObject(hThread, 5000);
    VirtualFreeEx(hProcess, pRemoteBuffer, Length(DllPath), MEM_RELEASE);
    CloseHandle(hThread);
  end;
end;

{ TInjectThread }

constructor TInjectThread.Create;
begin
  inherited Create(False);
  FreeOnTerminate:= True;
end;

procedure TInjectThread.Execute;
var
  hWindow, hProcess: THandle;
  ProcessId: DWORD;
begin
  while not Terminated do
  begin
    Sleep(1000);

    hWindow:= FindWindow('EVA_Window', '카카오톡');
    if hWindow = 0 then
      Continue;

    GetWindowThreadProcessId(hWindow, @ProcessId);
    if ProcessId = 0 then
      Continue;

    hProcess:= OpenProcess(PROCESS_ALL_ACCESS, False, ProcessId);
    InjectDll(hProcess, GetCurrentDir + '\hook.dll');
    Writeln(FormatDateTime('[yy-mm-dd hh:nn:ss] ', Now), 'inject ', ProcessId.ToHexString);
    CloseHandle(hProcess);

    while FindWindow('EVA_Window', '카카오톡') > 0 do
      Sleep(1000);
  end;
end;

begin
  try
    TInjectThread.Create;
    Readln;
  except
    on E: Exception do
      Writeln(E.ClassName, ': ', E.Message);
  end;
end.
