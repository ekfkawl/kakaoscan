unit uKakaoServer;

interface

uses
  Winapi.Windows, Winapi.Messages, System.SysUtils, System.Variants, System.Classes, Vcl.Graphics,
  Vcl.Controls, Vcl.Forms, Vcl.Dialogs, Vcl.StdCtrls, ProcessAPI, Generics.Collections, System.Generics.Defaults,
  System.Win.ScktComp, Vcl.ExtCtrls, ClientInstance, SharableMemory, KakaoAPI, System.JSON, StackTraceUtil, MD5, System.IOUtils,
  Vcl.Buttons, HttpUtils, uKey, IniFiles;

type
  TForm1 = class(TForm)
    ListBox1: TListBox;
    SS: TServerSocket;
    CheckBox1: TCheckBox;
    Timer1: TTimer;
    Label1: TLabel;
    CheckBox2: TCheckBox;
    BitBtn1: TBitBtn;
    procedure Timer1Timer(Sender: TObject);
    procedure SSClientRead(Sender: TObject; Socket: TCustomWinSocket);
    procedure SSClientError(Sender: TObject; Socket: TCustomWinSocket;
      ErrorEvent: TErrorEvent; var ErrorCode: Integer);
    procedure SSClientConnect(Sender: TObject; Socket: TCustomWinSocket);
    procedure SSClientDisconnect(Sender: TObject;
      Socket: TCustomWinSocket);
    procedure Log(s: String);
    procedure MacroThread;
    procedure FormCreate(Sender: TObject);
    procedure Button2Click(Sender: TObject);
    procedure CheckBox2Click(Sender: TObject);
    procedure BitBtn1Click(Sender: TObject);
  private
    { Private declarations }
    procedure InjectKakaoSDKThread;
  public
    { Public declarations }
  end;
var
  Form1: TForm1;

  Process: TProcess;
  KakaoHandle: THandle;
  Client: TClient;

  IsMacroRun: Boolean = True;

  ServerIndex: Integer;

  HostPath, CachePath: String;

implementation

{$R *.dfm}

// 디렉터리 파일 리스트
function FindFiles(const sPath: String; sMask: Array of String; slFiles: TStringList; Separator: String; bSubDir: Boolean): Integer;
var
  iFindResult: Integer;
  srSchRec: TSearchRec;
begin
  Result:= 0;

  for var Mask in sMask do
  begin
    iFindResult:= FindFirst(sPath + Mask, faAnyFile - faDirectory, srSchRec);
    while iFindResult = 0 do
    begin
      slFiles.Add(sPath.Split([Separator + '\'])[1] + {sPath + }srSchRec.Name);
      iFindResult:= FindNext(srSchRec);
    end;
    FindClose(srSchRec);

    if bSubDir then
    begin
      iFindResult:= FindFirst(sPath + '*.*', faDirectory, srSchRec);
      while iFindResult = 0 do
      begin
        if (srSchRec.Name <> '.') and (srSchRec.Name <> '..') then
          Result:= Result + FindFiles(sPath + srSchRec.Name + '\', sMask, slFiles, Separator, True);
        iFindResult:= FindNext(srSchRec);
      end;
      FindClose(srSchRec);
    end;
  end;
end;

procedure TForm1.Log(s: String);
begin
  TThread.Synchronize(TThread.CurrentThread, procedure
  begin
    if ListBox1.Items.Count > 100 then
      ListBox1.Items.Delete(0);

    ListBox1.Items.Add(Format('[%s] %s', [FormatDateTime('YY-MM-DD hh:mm:ss', now), s]));

    if CheckBox1.Checked then
      listBox1.TopIndex:= listBox1.Items.Count - 1;
  end);
end;

procedure TForm1.BitBtn1Click(Sender: TObject);
begin
  ListBox1.Clear;
end;

procedure TForm1.Button2Click(Sender: TObject);
begin
  TThread.CreateAnonymousThread(MacroThread).Start;
end;

procedure TForm1.CheckBox2Click(Sender: TObject);
begin
  IsMacroRun:= CheckBox2.Checked;
end;

procedure TForm1.Timer1Timer(Sender: TObject);
begin
  Label1.Caption:= 'ActiveConnections : ' + SS.Socket.ActiveConnections.ToString;
end;

procedure RemoveDataThread;
var
  Dirs, Files: TArray<String>;
  OlderThan: TDateTime;
begin
  while True do
  begin
    Sleep(60 * 1000);

    OlderThan:= Now() - 1/24;
    // 한시간 지난 디렉터리 삭제
    Dirs:= TDirectory.GetDirectories(ROOT);
    for var DName in Dirs do
    begin
      if TDirectory.GetCreationTime(DName) < OlderThan then
        TDirectory.Delete(DName, True);
    end;

    // 캐시 파일 삭제
    Files:= TDirectory.GetFiles(CachePath);
    for var DName in Files do
    begin
      if TFile.GetCreationTime(DName) < OlderThan then
        TFile.Delete(DName);
    end;
  end;
end;

procedure TForm1.FormCreate(Sender: TObject);
var
  Ini: TIniFile;
begin
  TThread.CreateAnonymousThread(InjectKakaoSDKThread).Start;
  TThread.CreateAnonymousThread(MacroThread).Start;
  TThread.CreateAnonymousThread(RemoveDataThread).Start;

  const IniPath = 'C:\config.ini';
  Ini:= TiniFile.Create(IniPath);
  try
    ServerIndex:= Ini.ReadString(APP_NAME, 'index', '0').ToInteger;
    CachePath:= Ini.ReadString(APP_NAME, 'cache', '0');
  finally
    Ini.Free;
  end;

  HostPath:= Format('https://storage%d.kakaoscan.com/%s', [ServerIndex, PATH_WEB]);
end;

procedure TForm1.InjectKakaoSDKThread;
begin
  // sdk inject
  if Process.GetProcessId('KakaoTalk.exe') then
  begin
    if Process.GetModuleBase(KakaoSDK32) = 0 then
    begin
      Process.InjectDll(GetCurrentDir + '\' + KakaoSDK32);
      Log('Inject SDK');
    end;
  end
  else begin
    MessageBox(0, '인젝션 실패 - 카카오톡 먼저 실행', '', $10);
    Exit;
  end;

  // 공유메모리 포인터 구함
  while True do
  begin
    Sleep(1000);

    PSharable:= GetSharableMemory;
    if Assigned(PSharable) then
    begin
      SharableInstance.Init(OpenProcess(PROCESS_ALL_ACCESS, False, Process.Id), PSharable);
      Log('Get SharableInstance ' + DWORD(PSharable).ToHexString);

      Exit;
    end;
  end;
end;

// connect
procedure TForm1.SSClientConnect(Sender: TObject; Socket: TCustomWinSocket);
begin
  try
    Client.DictMessage.AddOrSetValue(Socket.SocketHandle, TClientMessage.Create(GetTickCount64, GetTickCount64 + 3000, Socket.SocketHandle, Socket.RemoteAddress, ''));

    Log(Format('%d %s', [Socket.SocketHandle, '연결']));

    with SS.Socket do
    begin
      if ActiveConnections = 0 then
        Exit;

      for var i:= 0 to ActiveConnections - 1 do
      begin
        if Connections[i].SocketHandle <> Socket.SocketHandle then
        begin
          Connections[i].Close;
          Client.DictMessage.Remove(Connections[i].SocketHandle);
        end;
      end;
    end;
  except
    on E: Exception do
    begin
      Log(Format('# %s: %s', [E.ClassName, E.Message]));
      Log(StackTrace(E.StackInfo));
    end;
  end;
end;

// disconnect
procedure TForm1.SSClientDisconnect(Sender: TObject; Socket: TCustomWinSocket);
begin
  try
    if Client.DictMessage.ContainsKey(Socket.SocketHandle) then
    begin
      with Client.DictMessage.Items[Socket.SocketHandle] do
      begin
        if IsSendReady then
        begin
          Socket.SendText(AnsiString(WebResponMsg));
        end;
      end;
    end;

    Client.DictMessage.Remove(Socket.SocketHandle);

    Log(Format('%d %s', [Socket.SocketHandle, '연결 해제']));
  except
    on E: Exception do
    begin
      Log(Format('# %s: %s', [E.ClassName, E.Message]));
      Log(StackTrace(E.StackInfo));
    end;
  end;
end;

procedure TForm1.SSClientError(Sender: TObject; Socket: TCustomWinSocket; ErrorEvent: TErrorEvent; var ErrorCode: Integer);
begin
  case ErrorCode of
    10053:
    Socket.Close;
  end;
  ErrorCode:= 0;
end;

// 메세지 수신
procedure TForm1.SSClientRead(Sender: TObject; Socket: TCustomWinSocket);
var
  Session, Number, RemoteAddr: String;
  Value: Int64;
  ClientMessage: TClientMessage;
begin
  try
    ClientMessage:= Client.DictMessage.Items[Socket.SocketHandle];
    if ClientMessage.Heartbeat < GetTickCount64 then
    begin
      Socket.Close;
      Log(Format('%d %s', [Socket.SocketHandle, '타임아웃 연결 해제']));
    end;

    const SReceiveText = String(Socket.ReceiveText);

    // client respon
    if Pos('Profile:', SReceiveText) > 0 then
    begin
//      Number:= SReceiveText.Split(['Profile:', '['])[2];
      Number:= SReceiveText.Split(['Profile:', '<'])[1];

      if not TryStrToInt64(Number, Value) then
      begin
        Socket.Close;
        Exit;
      end;

      Session:= SReceiveText.Split(['[', ']'])[1];

      RemoteAddr:= SReceiveText.Split(['<', '>'])[1];

      Socket.SendText(AnsiString(Format('%s:', [Session])));


      ClientMessage.Msg:= Number;
      ClientMessage.Session:= Session;
      ClientMessage.RemoteAddr2:= RemoteAddr;

      Log(Format('%s(%d) %s', [Socket.RemoteAddress, Socket.SocketHandle, SReceiveText]));
    end;

    ClientMessage.Heartbeat:= GetTickCount64 + 3000;

    Client.DictMessage.AddOrSetValue(Socket.SocketHandle, ClientMessage);
  except
    on E: Exception do
    begin
      Log(Format('# %s: %s', [E.ClassName, E.Message]));
      Log(StackTrace(E.StackInfo));
    end;
  end;
end;

procedure TForm1.MacroThread;
var
//  ClientList: TList<TClientMessage>;
  ClientMessage: TClientMessage;
  JSONObject: TJSONObject;
  JSONArray1, JSONArray2, JSONArray3: TJSONArray;
  sl: TStringList;
  TimeOut: LONG64;
  CurrentSocketHandle: NativeInt;
begin
  while True do
  begin
    Sleep(250);

    with SS.Socket do
    begin
      if ActiveConnections = 0 then
        Continue;

      for var i:= 0 to ActiveConnections - 1 do
      begin
        try
          CurrentSocketHandle:= Connections[i].SocketHandle;

          if Client.DictMessage.ContainsKey(Connections[i].SocketHandle) then
          begin
//            ClientList:= Client.ToQueueList;
            JSONObject:= TJSONObject.Create;
            JSONArray1:= TJSONArray.Create;
            JSONArray2:= TJSONArray.Create;
            JSONArray3:= TJSONArray.Create;
            try
              if not IsMacroRun then
                Continue;

//              if ClientList.Count = 0 then
//                Continue;

//              if Length(ClientList[0].Session) = 0 then
//                Continue;

              // 첫번째 대기열이 아님
//              if not Connections[i].SocketHandle = ClientList[0].SocketHandle then
//              begin
//                Log(Format('Connections[%d] RemoteAddress Not Equals', [i]));
//                Continue;
//              end;

              const Current = Client.DictMessage.Items[Connections[i].SocketHandle];
              if Length(Current.Session) = 0 then
                Continue;

              // 요청 메세지가 비어있음
              if Current.Msg.Equals('') then
              begin
//                Log(Format('Connections[%d] Msg.Equals('')', [i]));
                Continue;
              end;

              if Current.IsSendReady then
              begin
                Log(Format('Connections[%d] IsSendReady Error', [i]));
                Connections[i].Close;
                Continue;
              end;


              ClientMessage:= Client.DictMessage.Items[Current.SocketHandle];

              // 첫번째 대기열이 60초 이상 유지되면 예외로 간주하고 연결 끊음
//              if Current.QueueTimeOut = 0 then
//              begin
////                Log(Format('Connections[%d] Set TimeOut', [i]));
//                ClientMessage.QueueTimeOut:= GetTickCount64 + 70 * 1000;
//                Client.DictMessage.AddOrSetValue(Current.SocketHandle, ClientMessage);
//              end
//              else if GetTickCount64 > ClientMessage.QueueTimeOut then
//              begin
//                Connections[i].Close;
//                Log(Format('Connections[%d] TimeOut Close', [i]));
//              end;


              //
              try

                //
                var IsContinue:= False;

                for var j:= 1 to 2 do
                begin
                  if not AddFriend(Current.Msg) then
                  begin
                    Log(Format('Connections[%d] Error AddFriend 2', [i]));
                    break;
                  end;
                  Sleep(1000);

                  if not Client.DictMessage.TryGetValue(Current.SocketHandle, ClientMessage) then
                  begin
                    Log(Format('Connections[%d] Exit AddFriend', [i]));
                    IsContinue:= True;
                    break;
                  end;

                  if SharableInstance.GetAddFriendResult <> ADD_NONE then
                  begin
                    break;
                  end;
                end;

                if IsContinue then
                begin
                  Continue;
                end;
                //`

                case SharableInstance.GetAddFriendResult of
                  ADD_NONE:
                  begin
                    Log(Format('Connections[%d] GetAddFriendResult = 0', [i]));
                    JSONObject.AddPair('Error', '친구 동기화에 실패하였습니다. 다시 시도해주세요');
                    Continue;
                  end;

                  ADD_BAN_USER:
                  begin
                    JSONObject.AddPair('Error', '이용이 정지된 전화번호입니다');
                    Continue;
                  end;

                  ADD_FAIL:
                  begin
                    JSONObject.AddPair('Error', '유효하지 않은 전화번호입니다. 다시 시도해주세요');
                    Continue;
                  end;
                end;

                //
                if not SearchFriend(Current.Msg) then
                begin
                  Log(Format('Connections[%d] Error SearchFriend', [i]));
                  Continue;
                end;
                TimeOut:= GetTickCount64 + 1000;
                while SharableInstance.GetSearchCount <> 1 do
                begin
                  Sleep(100);
                  if GetTickCount64 > TimeOut then
                  begin
                    break;
                  end;
                end;
                //`

                // use count++
                HttpPost(Format('/use?remoteAddress=%s&key=%s', [ClientMessage.RemoteAddr2, HTTP_KEY]));
                HttpPost(Format('/limit?serverIndex=%d&key=%s', [ServerIndex, HTTP_KEY]));
                //`

                if not ViewFriend then
                begin
                  JSONObject.AddPair('Error', '프로필을 찾지 못했습니다');
                  Log(Format('Connections[%d] Error ViewFriend', [i]));
                  Continue;
                end;
                Sleep(500);

                var OriginName:= SharableInstance.GetFriendOriginName;
                if OriginName.Equals('') then
                  OriginName:= '이름 없음';

                JSONObject.AddPair('OriginName', OriginName);

                JSONObject.AddPair('IsEmptyProfile', SharableInstance.IsEmptyProfileImage.ToInteger);

                JSONObject.AddPair('IsEmptyBackgroundImage', SharableInstance.IsEmptyBackgroundImage.ToInteger);

                JSONObject.AddPair('ProfileImageUrl', SharableInstance.GetProfileImageUrl);

                JSONObject.AddPair('StatusMessage', SharableInstance.GetStatusMessage);

                JSONObject.AddPair('MusicName', SharableInstance.GetMusicName);

                JSONObject.AddPair('ArtistName', SharableInstance.GetArtistName);

                JSONObject.AddPair('MusicAlbumUrl', SharableInstance.GetMusicAlbumUrl);
                //`


                //
                if not ViewProfileImage(SharableInstance.GetFriendCustomName) then
                begin
                  Log(Format('Connections[%d] Error Or Empty ViewProfileImage', [i]));
                  Continue;
                end;
//                Sleep(1000);

                if not Client.DictMessage.ContainsKey(Current.SocketHandle) then
                begin
                  Log(Format('Connections[%d] Exit ViewProfileImage', [i]));
                  Continue;
                end;
                //`

                const MD5FriendCustomName = StrToMD5(AnsiString(SharableInstance.GetFriendCustomName));

                JSONObject.AddPair('Host', Format('%s%s/', [HostPath, MD5FriendCustomName]));

                const Dir = Format('%s%s\', [ROOT, MD5FriendCustomName]);

                // profile
                sl:= TStringList.Create;
                try
                  FindFiles(Format('%s%s\', [Dir, IIS_PROFILE_PATH]), ['*.jpg'], sl, MD5FriendCustomName, False);
                  for var s in sl do
                  begin
                    const v = s.Split(['\', '.']);
                    JSONArray1.Add(TJSONObject.Create.AddPair('Dir', v[0]).AddPair('Name', v[1]) );
                  end;
                finally
                  JSONObject.AddPair('ImageUrlCount', sl.Count);
                  JSONObject.AddPair('ImageUrl', JSONArray1);
                  sl.Free;
                end;

                // bg
                sl:= TStringList.Create;
                try
                  FindFiles(Format('%s%s\', [Dir, IIS_BG_PATH]), ['*.jpg'], sl, MD5FriendCustomName, False);
                  for var s in sl do
                  begin
                    const v = s.Split(['\', '.']);
                    JSONArray2.Add(TJSONObject.Create.AddPair('Dir', v[0]).AddPair('Name', v[1]));
                  end;
                finally
                  JSONObject.AddPair('BgImageUrlCount', sl.Count);
                  JSONObject.AddPair('BgImageUrl', JSONArray2);
                  sl.Free;
                end;

                // mp4
                sl:= TStringList.Create;
                try
                  FindFiles(Dir, ['*.mp4'], sl, MD5FriendCustomName, True);
                  for var s in sl do
                  begin
                    const v = s.Split(['\', '.']);
                    JSONArray3.Add(TJSONObject.Create.AddPair('Dir', v[0]).AddPair('Name', v[1]) );
                  end;
                finally
                  JSONObject.AddPair('VideoCount', sl.Count);
                  JSONObject.AddPair('VideoUrl', JSONArray3);
                  sl.Free;
                end;

                if not BlockAndClearFriend then
                begin
                  Log(Format('Connections[%d] Error BlockAndClearFriend', [i]));
                  Continue;
                end;

                if not Client.DictMessage.ContainsKey(Current.SocketHandle) then
                begin
                  Log(Format('Connections[%d] Exit Final', [i]));
                  Continue;
                end;
                //`

              finally
                try
                  if CurrentSocketHandle = Connections[i].SocketHandle then
                  begin
                    ClientMessage.WebResponMsg:= Format('%s:%s', [ClientMessage.Session, JSONObject.ToString]);
                    ClientMessage.IsSendReady:= True;
                    Client.DictMessage.AddOrSetValue(Current.SocketHandle, ClientMessage);

                    Connections[i].Close;

                    Log(Format('%s(%s) 응답 완료', [Current.RemoteAddr2, Current.Msg]));
                  end;
                except;
                  Log(Format('%d Already Close', [CurrentSocketHandle]));
                end;
              end;
              //`

            finally
//              ClientList.Free;
              JSONObject.Free;
            end;

          end;

        except
          on E: Exception do
          begin
            Log(Format('# %s: %s', [E.ClassName, E.Message]));
            Log(StackTrace(E.StackInfo));
          end;
        end;
      end;
    end;
  end;
end;

initialization
//  allocconsole;
  Client:= TClient.Create;
  KakaoHandle:= FindWindow('EVA_Window_Dblclk', '카카오톡');

finalization
  Client.Free;

end.
