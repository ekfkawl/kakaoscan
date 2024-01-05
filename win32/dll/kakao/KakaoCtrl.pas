unit KakaoCtrl;

interface

uses
  Winapi.Windows, Winapi.Messages, System.Classes, System.SysUtils, System.Threading,
  KakaoHandle, KakaoHook, KakaoEnumCallback, KakaoResponse;

type
  TKakaoCtrl = class(TKakaoHandle)
  private
    constructor Create;
  public
    class function GetInstance: TKakaoCtrl;
    function AddFriend(PhoneNumber: string): IFuture<TKakaoResponse>;
    function SearchFriend(FriendName: string): IFuture<boolean>;
    function ViewFriend: IFuture<string>;
    procedure SynchronizationFriend;
  end;

implementation

var
  SingletonInstance: TKakaoCtrl;

{ TKakaoCtrl }

procedure Click(hWindow: THandle; x, y: Integer);
begin
  SendMessage(hWindow, WM_LBUTTONDOWN, MK_LBUTTON, MAKELPARAM(x, y));
  SendMessage(hWindow, WM_LBUTTONUP, MK_LBUTTON, MAKELPARAM(x, y));
end;

constructor TKakaoCtrl.Create;
begin
  inherited Create;
end;

class function TKakaoCtrl.GetInstance: TKakaoCtrl;
begin
  if not Assigned(SingletonInstance) then
    SingletonInstance:= TKakaoCtrl.Create;
  Result:= SingletonInstance;
end;

function TKakaoCtrl.AddFriend(PhoneNumber: string): IFuture<TKakaoResponse>;
var
  Res: TKakaoResponse;
  Tick: UInt64;
  EnumInfo: TEnumInfo;
begin
  Res.ResponseType:= rtUnknown;

  Result:= TTask.Future<TKakaoResponse>(function: TKakaoResponse
  begin
    EnumInfo:= TEnumInfo.Create(FKakao);
    Tick:= GetTickCount64 + 2000;
    while EnumInfo.FoundHandle = 0 do
    begin
      if Tick < GetTickCount64 then
        Exit(Res);

      EnumWindows(@AddFriendWindow, LPARAM(@EnumInfo));
      if EnumInfo.FoundHandle = 0 then
      begin
        Click(FContactListView, 290, 30);
        Sleep(250);
        Continue;
      end;

      break;
    end;

    var AddFriendFrameHandle:= FindWindowEx(EnumInfo.FoundHandle, 0, '#32770', nil);
    if AddFriendFrameHandle = 0 then
      Exit(Res);

    AddFriendFrameHandle:= FindWindowEx(EnumInfo.FoundHandle, AddFriendFrameHandle, '#32770', nil);
    const EditFriendNameHandle = FindWindowEx(AddFriendFrameHandle, 0, 'Edit', nil);
    const EditPhoneNumberHandle = FindWindowEx(AddFriendFrameHandle, EditFriendNameHandle, 'Edit', nil);
    SendMessage(EditFriendNameHandle, WM_SETTEXT, 0, LParam(PChar(PhoneNumber)));
    SendMessage(EditPhoneNumberHandle, WM_SETTEXT, 0, LParam(PChar(PhoneNumber)));

    Sleep(250);
    PostMessage(AddFriendFrameHandle, WM_KEYDOWN, VK_RETURN, 0);
    Sleep(250);
    SendMessage(EnumInfo.FoundHandle, WM_CLOSE, 0, 0);

    Exit(GetRecentKakaoResponse);
  end);
end;

function TKakaoCtrl.SearchFriend(FriendName: string): IFuture<boolean>;
var
  Tick: UInt64;
begin
  Result:= TTask.Future<boolean>(function: boolean
  begin
    SendMessage(FSearchEdit, WM_SETTEXT, 0, LParam(PChar('___________')));
    Tick:= GetTickCount64 + 1000;
    while Tick > GetTickCount64 do
    begin
      if GetSearchCount = 0 then
        break;
    end;

    SendMessage(FSearchEdit, WM_SETTEXT, 0, LParam(PChar(FriendName)));
    Tick:= GetTickCount64 + 1000;
    while Tick > GetTickCount64 do
    begin
      if GetSearchCount = 1 then
        Exit(True);
    end;
    Exit(False);
  end);
end;

function TKakaoCtrl.ViewFriend: IFuture<string>;
var
  EnumInfo: TEnumInfo;
  Tick: UInt64;
begin
  Result:= TTask.Future<string>(function: string
  begin
    EnumInfo:= TEnumInfo.Create(FKakao);
    Tick:= GetTickCount64 + 2000;
    while EnumInfo.FoundHandle = 0 do
    begin
      if Tick < GetTickCount64 then
        Exit('');

      Click(FSearchListCtrl, 40, 50);
      Sleep(500);
      EnumWindows(@ViewFriendWindow, LPARAM(@EnumInfo));
    end;
    SendMessage(EnumInfo.FoundHandle, WM_CLOSE, 0, 0);
    Result:= GetRecentFriendViewName;
  end);
end;

procedure TKakaoCtrl.SynchronizationFriend;
var
  EnumInfo: TEnumInfo;
begin
  EnumWindows(@ConfigFriendTabWindow, LPARAM(@EnumInfo));
  if EnumInfo.FoundHandle > 0 then
  begin
    for var i:= 1 to 3 do
    begin
      Click(GetParent(EnumInfo.FoundHandle), 70, 125);
      Sleep(Random(100));
    end;
  end;
end;

initialization
  SingletonInstance:= nil;

finalization
  SingletonInstance.Free;


end.

