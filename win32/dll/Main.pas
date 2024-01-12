unit Main;

interface

uses
  Winapi.Windows, System.Threading, System.Classes, System.SysUtils,
  KakaoHandle, KakaoCtrl, KakaoResponse, KakaoStatus, KakaoHook, KakaoProfile, RedisUtil, SearchEvent, EventStatus, Test, GuardObjectUtil;

procedure Initialize;
procedure RunEvent(const EventId, PhoneNumber: string);

implementation

var
  IsRunning: Boolean;
  KakaoCtrl: TKakaoCtrl;
  Redis: TRedis;

procedure MergeFeeds(HasFeeds: Boolean; ScanType: Integer; var KakaoProfile: TKakaoProfile);
var
  FeedsContainer: TFeedsContainer;
begin
  if HasFeeds then
  begin
    Guard(FeedsContainer, KakaoCtrl.Scan(ScanType).Value);
    if Assigned(FeedsContainer) then
    begin
      KakaoProfile.Profile.ProfileFeeds.Merge(FeedsContainer);
      CleanUpMemory;
    end;
  end;
end;

procedure RunEvent(const EventId, PhoneNumber: string);
var
  KakaoResponse: TKakaoResponse;
  KakaoStatus: TKakaoStatus;
  KakaoProfile: TKakaoProfile;
  StatusResponse: TStatusResponse;
  ViewFriendInfo: TViewFriendInfo;
begin
  Redis.SetEventStatus(EventId, TEventStatus.CreateInstance(EVENT_PROCESSING, ''));

  if IsRunning then
    Exit;
  IsRunning:= True;

  TTask.Run(
    procedure
    begin
      const StartTick = GetTickCount64;
      try
        const Tick = GetTickCount64 + 1000;
        while (Tick > GetTickCount64) and (not Assigned(Redis.GetEventStatus(EventId))) do
          Sleep(1);

        if not KakaoCtrl.SearchFriend(PhoneNumber).Value then
        begin
          KakaoResponse:= KakaoCtrl.AddFriend(PhoneNumber).Value;
          KakaoCtrl.SynchronizationFriend;

          if KakaoResponse.ResponseType <> rtFriend then
          begin
            Redis.SetEventStatus(EventId, TEventStatus.CreateInstance(EVENT_FAILURE, FAILURE_ADD_FRIEND));
            Exit;
          end;

          Guard(KakaoStatus, TKakaoStatus.Create(KakaoResponse.Json));
          StatusResponse:= KakaoStatus.GetStatusResponse;

          if KakaoStatus.Status = STATUS_FAILURE then
          begin
            Redis.SetEventStatus(EventId, TEventStatus.CreateInstance(EVENT_FAILURE, StatusResponse.Message));
            Exit;
          end else
          if (KakaoStatus.Status = STATUS_OK) and (not KakaoCtrl.SearchFriend(PhoneNumber).Value) then
          begin
            Redis.SetEventStatus(EventId, TEventStatus.CreateInstance(EVENT_FAILURE, DELAYED_FRIEND_SYNC));
            Exit;
          end;
        end;

        ViewFriendInfo:= KakaoCtrl.ViewFriend.Value;
        KakaoResponse:= GetRecentKakaoResponse;
        if KakaoResponse.ResponseType = rtProfile then
        begin
          const HasProfile = KakaoResponse.HasProfile;
          const HasBackground = KakaoResponse.HasBackground;

          Guard(KakaoProfile, TKakaoProfile.Create(KakaoResponse.Json));
          KakaoProfile.Profile.NickName:= ViewFriendInfo.Name;
          KakaoProfile.Profile.ScreenBase64:= ViewFriendInfo.ScreenToBase64;
          Redis.SetEventStatus(EventId, TEventStatus.CreateInstance(EVENT_PROCESSING, Format(FRIEND_PROFILE_SCANNING, [ViewFriendInfo.Name])));

          MergeFeeds(HasProfile, 0, KakaoProfile);
          MergeFeeds(HasBackground, 1, KakaoProfile);

          Redis.SetEventStatus(EventId, TEventStatus.CreateInstance(EVENT_SUCCESS, KakaoProfile.ToJSON));
          Writeln(#9'state: ', EVENT_SUCCESS);
        end;

      finally
        IsRunning:= False;
        Writeln(#9'sec: ', FloatToStr((GetTickCount64 - StartTick) / 1000));
      end;
    end
  )
end;

procedure Initialize;
begin
  KakaoCtrl:= TKakaoCtrl.GetInstance;
  Redis:= TRedis.GetInstance;

  {$IFDEF DEBUG}
  TThread.CreateAnonymousThread(
    procedure
    begin
      Form1:= TForm1.Create(nil);
      Form1.ShowModal;
    end
  ).Start;

  ReportMemoryLeaksOnShutdown:= True;
  {$ENDIF}
end;

end.
