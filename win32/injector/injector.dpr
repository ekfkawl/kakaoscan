program injector;

{$APPTYPE CONSOLE}

{$R *.res}

uses
  System.SysUtils,
  System.Classes,
  Winapi.ShellAPI,
  Winapi.Messages,
  TlHelp32,
  Windows,
  KakaoEnumCallback in '..\dll\kakao\private\KakaoEnumCallback.pas';

type TInjectThread = class(TThread)
private
protected
  procedure Execute; override;
public
  constructor Create;
end;

type TRedoThread = class(TThread)
private
protected
  procedure Execute; override;
public
  Kakao, OnlineMainView: THandle;
  constructor Create;
end;

var
  IsReadyInject: Boolean = True;

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

function TerminateProcess(const ProcName: String): Integer;
const
  PROCESS_TERMINATE = $0001;
var
  ContinueLoop: BOOL;
  FSnapshotHandle: THandle;
  FProcessEntry32: TProcessEntry32;
begin
  Result:= 0;
  FSnapshotHandle:= CreateToolhelp32Snapshot(TH32CS_SNAPPROCESS, 0);
  FProcessEntry32.dwSize:= SizeOf(FProcessEntry32);
  ContinueLoop := Process32First(FSnapshotHandle, FProcessEntry32);
  while Integer(ContinueLoop) <> 0 do
  begin
    if (UpperCase(ExtractFileName(FProcessEntry32.szExeFile)) = uppercase(ProcName)) then
    begin
      Result:= Integer(Windows.TerminateProcess(OpenProcess(PROCESS_TERMINATE, BOOL(0), FProcessEntry32.th32ProcessID), 0));
    end;
    ContinueLoop:= Process32Next(FSnapshotHandle, FProcessEntry32);
  end;
  CloseHandle(FSnapshotHandle);
end;

function ExistsProcess(exeFileName: String): Boolean;
var
  ContinueLoop: BOOL;
  FSnapshotHandle: THandle;
  FProcessEntry32: TProcessEntry32;
begin
  FSnapshotHandle:= CreateToolhelp32Snapshot(TH32CS_SNAPPROCESS, 0);
  FProcessEntry32.dwSize:= SizeOf(FProcessEntry32);
  ContinueLoop:= Process32First(FSnapshotHandle, FProcessEntry32);
  Result:= False;
  while Integer(ContinueLoop) <> 0 do
  begin
    if ((UpperCase(ExtractFileName(FProcessEntry32.szExeFile)) =
      UpperCase(ExeFileName)) or (UpperCase(FProcessEntry32.szExeFile) =
      UpperCase(ExeFileName))) then
    begin
      Result:= True;
    end;
    ContinueLoop:= Process32Next(FSnapshotHandle, FProcessEntry32);
  end;
  CloseHandle(FSnapshotHandle);
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

    if not IsReadyInject then
      Continue;

    hWindow:= FindWindow('EVA_Window_Dblclk', '카카오톡');
    if hWindow = 0 then
      Continue;

    GetWindowThreadProcessId(hWindow, @ProcessId);
    if ProcessId = 0 then
      Continue;

    hProcess:= OpenProcess(PROCESS_ALL_ACCESS, False, ProcessId);
    InjectDll(hProcess, GetCurrentDir + '\hook.dll');
    Writeln(FormatDateTime('[yy-mm-dd hh:nn:ss] ', Now), 'inject ', ProcessId.ToHexString);
    CloseHandle(hProcess);

    while FindWindow('EVA_Window_Dblclk', '카카오톡') > 0 do
      Sleep(1000);
  end;
end;

function RunAsAdmin(hWnd: hWnd; filename: string; Parameters: string; Visible: Boolean = true): Boolean;
var
  sei: TShellExecuteInfo;
begin
  ZeroMemory(@sei, SizeOf(sei));
  sei.cbSize:= SizeOf(TShellExecuteInfo);
  sei.Wnd:= hWnd;
  sei.fMask:= SEE_MASK_FLAG_DDEWAIT or SEE_MASK_FLAG_NO_UI;
  sei.lpVerb:= PChar('runas');
  sei.lpFile:= PChar(filename);
  if Parameters <> '' then
    sei.lpParameters:= PChar(Parameters);
  if Visible then
    sei.nShow:= SW_SHOWNORMAL
  else
    sei.nShow:= SW_HIDE;

  Result:= ShellExecuteEx(@sei);
end;

procedure LClick(hWindow: THandle; x, y: Integer);
begin
  SendMessage(hWindow, WM_LBUTTONDOWN, MK_LBUTTON, MAKELPARAM(x, y));
  SendMessage(hWindow, WM_LBUTTONUP, MK_LBUTTON, MAKELPARAM(x, y));
end;


{ TRedoThread }

constructor TRedoThread.Create;
begin
  inherited Create(False);
  FreeOnTerminate:= True;
end;

procedure TRedoThread.Execute;
var
  EnumInfo: TEnumInfo;
begin
  while not Terminated do
  begin
    Sleep(1000);

    if (FindWindow(nil, 'CrashReporter') > 0) And (ExistsProcess('CrashReporter.exe')) then
    begin
      IsReadyInject:= False;
      TerminateProcess('KakaoTalk.exe');
      TerminateProcess('CrashReporter.exe');

      Sleep(2000);
      RunAsAdmin(0, 'C:\Program Files (x86)\Kakao\KakaoTalk\KakaoTalk.exe', '');

      Kakao:= 0;
      while (not ExistsProcess('KakaoTalk.exe')) or (Kakao = 0) do
      begin
        Sleep(1000);
        Kakao:= FindWindow('EVA_Window_Dblclk', '카카오톡');
      end;

      EnumInfo.FoundHandle:= 0;
      while EnumInfo.FoundHandle = 0 do
      begin
        OnlineMainView:= FindWindowEx(Kakao, 0, 'EVA_ChildWindow', nil);
        LClick(OnlineMainView, 30, 335);
        Sleep(250);
        const hFriendConfig = FindWindow('EVA_Menu', nil);
        if hFriendConfig > 0 then
        begin
          LClick(hFriendConfig, 20, 20);
        end;

        Sleep(1000);

        EnumWindows(@ConfigWindow, LPARAM(@EnumInfo));
        IsReadyInject:= True;
      end;

      LClick(EnumInfo.FoundHandle, 38, 213);
    end;
  end;
end;

begin
  try
    TInjectThread.Create;
    TRedoThread.Create;
    Readln;
  except
    on E: Exception do
      Writeln(E.ClassName, ': ', E.Message);
  end;
end.
