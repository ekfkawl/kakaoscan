unit KakaoHandle;

interface

uses
  Winapi.Windows;

type
  TKakaoHandle = class
  protected
    FKakao,
    FOnlineMainView,
    FContactListView,
    FSearchListCtrl,
    FSearchEdit: THandle;

    constructor Create;
  public
    class function GetInstance: TKakaoHandle;
  end;

implementation

var
  SingletonInstance: TKakaoHandle;

{ TKakaoHandle }

constructor TKakaoHandle.Create;
begin
  FKakao:= FindWindow('EVA_Window_Dblclk', '카카오톡');
  FOnlineMainView:= FindWindowEx(FKakao, 0, 'EVA_ChildWindow', nil);
  FContactListView:= FindWindowEx(FOnlineMainView, 0, 'EVA_Window', nil);
  FSearchListCtrl:= FindWindowEx(FContactListView, 0, 'EVA_VH_ListControl_Dblclk', nil);
  FSearchEdit:= FindWindowEx(FContactListView, 0, 'Edit', nil);

  if (FKakao = 0) or (FOnlineMainView = 0) or (FContactListView = 0) or (FSearchListCtrl = 0) then
    Writeln('kakao handle lookup failed')
  else
    Writeln('kakao handle lookup success');
end;

class function TKakaoHandle.GetInstance: TKakaoHandle;
begin
  if not Assigned(SingletonInstance) then
    SingletonInstance:= TKakaoHandle.Create;
  Result:= SingletonInstance;
end;

initialization
  SingletonInstance:= nil;

finalization
  SingletonInstance.Free;

end.
