unit Main;

interface

uses
  Winapi.Windows, System.Threading, System.Classes, System.SysUtils,
  KakaoHandle, KakaoCtrl, KakaoResponse, KakaoFriend, KakaoId, KakaoParent, KakaoHook, KakaoProfile, RedisUtils, SearchEvent, EventStatus, Test, GuardObjectUtils,
  InvalidPhoneNumber, SearchNewPhoneNumberEvent, RedisConfig, KakaoEnumCallback, LogUtils;

procedure Initialize;
procedure RunEvent(const EventId, Email, PhoneNumber: string; IsId: boolean);

implementation

var
  IsRunning: Boolean;
  KakaoCtrl: TKakaoCtrl;
  Redis: TRedis;

procedure MergeFeeds(HasFeeds: Boolean; hViewFriendWindow: THandle; ImageViewerType: TProfileImageViewerType; var KakaoProfile: TKakaoProfile);
var
  FeedsContainer: TFeedsContainer;
begin
  if HasFeeds then
  begin
    Guard(FeedsContainer, KakaoCtrl.Scan(hViewFriendWindow).Value);
    if Assigned(FeedsContainer) then
    begin
      if ImageViewerType = rtViewTypeProfile then
        KakaoProfile.Profile.ProfileFeeds.Merge(FeedsContainer)
      else
        KakaoProfile.Profile.BackgroundFeeds.Merge(FeedsContainer);

      CleanUpMemory;
    end;
  end;
end;

procedure RunEvent(const EventId, Email, PhoneNumber: string; IsId: boolean);
var
  EnumInfo: TEnumInfo;
  KakaoResponse: TKakaoResponse;
  KakaoParent: TKakaoParent;
  OpenIdResult: TOpenIdResult;
  SearchNewNumberEvent: TSearchNewPhoneNumberEvent;
  SearchEvent: TSearchEvent;
  KakaoProfile: TKakaoProfile;
  StatusResponse: TStatusResponse;
  ViewFriendInfo: TViewFriendInfo;
  EventStatus: TEventStatus;
  LogMsg: String;
begin
  const SetEventStatusAndPublish: TProc<string, string> = procedure(EventStatus, ResponseMessage: string)
  begin
    Redis.SetEventStatus(EventId, TEventStatus.CreateInstance(EventStatus, ResponseMessage));
    Guard(SearchEvent, TSearchEvent.Create(EventId, Email, PhoneNumber, IsId));
    Redis.Publish(EVENT_TRACE_TOPIC, SearchEvent.ToEventJSON);
  end;

  SetEventStatusAndPublish(EVENT_PROCESSING, '');

  if IsRunning then
    Exit;
  IsRunning:= True;

  TTask.Run(
    procedure
    begin
      LogMsg:= '';
      LogMsg:= LogMsg + Format('searchEvent'#10#9'eventId: %s'#10#9'email: %s'#10#9'phoneNumber: %s', [EventId, Email, PhoneNumber]) + sLineBreak;

      EnumInfo:= TEnumInfo.Create(TKakaoHandle.GetInstance.Kakao);
      EnumWindows(@CloseDialogWindow, LPARAM(@EnumInfo));
      EnumWindows(@CloseAddFriendWindow, LPARAM(@EnumInfo));

      const StartTick = GetTickCount64;
      try
        const Tick = GetTickCount64 + 1000;
        while Tick > GetTickCount64 do
        begin
          Guard(EventStatus, Redis.GetEventStatus(EventId));
          if Assigned(Redis.GetEventStatus(EventId)) then
            break;
          Sleep(1);
        end;

        if (not IsId) and (not KakaoCtrl.SearchFriend(PhoneNumber).Value) then
        begin
          KakaoResponse:= KakaoCtrl.AddFriend(PhoneNumber, rtNumber).Value;
          Guard(KakaoParent, TKakaoParent.Create(KakaoResponse.ParentJson));
          StatusResponse:= KakaoParent.GetStatusResponse;

          KakaoCtrl.SynchronizationFriend;

          if StatusResponse.Status = STATUS_FAILURE then
          begin
            SetEventStatusAndPublish(EVENT_FAILURE, StatusResponse.Message);

            Redis.CacheInvalidPhoneNumber(PhoneNumber, TInvalidPhoneNumber.CreateInstance(Email));
            LogMsg:= LogMsg + #9'isInvalid: TRUE' + sLineBreak;

            Exit;
          end
          else if StatusResponse.Status = STATUS_OK then
          begin
            Guard(SearchNewNumberEvent, TSearchNewPhoneNumberEvent.Create(Email, PhoneNumber));
            Redis.Publish(OTHER_EVENT_TOPIC, SearchNewNumberEvent.ToEventJSON);
            LogMsg:= LogMsg + #9'isNewPhoneNumber: TRUE' + sLineBreak;

            if not KakaoCtrl.SearchFriend(PhoneNumber).Value then
            begin
              SetEventStatusAndPublish(EVENT_FAILURE, DELAYED_FRIEND_SYNC);
              Exit;
            end;
          end
          else begin
            SetEventStatusAndPublish(EVENT_FAILURE, FAILURE_ADD_FRIEND);
            Exit;
          end;
        end
        else if IsId then
        begin
          KakaoResponse:= KakaoCtrl.AddFriend(PhoneNumber, rtId).Value;
          if KakaoResponse.ResponseType <> rtStatus then
          begin
            SetEventStatusAndPublish(EVENT_FAILURE, FAILURE_ADD_FRIEND);
            Exit;
          end;

          Guard(KakaoParent, TKakaoParent.Create(KakaoResponse.ParentJson));
          if (KakaoParent.Status = STATUS_FAILURE_QUERY) or (not KakaoParent.IsQueryPresent) then
          begin
            SetEventStatusAndPublish(EVENT_FAILURE, INVALID_KAKAO_ID);
            Redis.CacheInvalidPhoneNumber(PhoneNumber, TInvalidPhoneNumber.CreateInstance(Email));
            LogMsg:= LogMsg + #9'isInvalid: TRUE' + sLineBreak;
            Exit;
          end;

          OpenIdResult:= KakaoCtrl.OpenId.Value;

          if not OpenIdResult.IsOpened then
          begin
            SetEventStatusAndPublish(EVENT_FAILURE, FAILURE_ADD_FRIEND);
            Exit;
          end;

          if not OpenIdResult.HasFriendId then
          begin
            Guard(SearchNewNumberEvent, TSearchNewPhoneNumberEvent.Create(Email, '@' + PhoneNumber));
            Redis.Publish(OTHER_EVENT_TOPIC, SearchNewNumberEvent.ToEventJSON);
            LogMsg:= LogMsg + #9'isNewKakaoId: TRUE' + sLineBreak;
          end;
        end;

        SetEventStatusAndPublish(EVENT_PROCESSING, FRIEND_PROFILE_SCAN_START);
        ViewFriendInfo:= KakaoCtrl.ViewFriend(Byte(IsId)).Value;
        if ViewFriendInfo.Name = '' then
        begin
          SetEventStatusAndPublish(EVENT_FAILURE, FAILURE_ADD_FRIEND);
          Exit;
        end;

        KakaoResponse:= GetRecentKakaoResponse;
        if KakaoResponse.ResponseType = rtProfile then
        begin
          const HasProfile = KakaoResponse.HasProfile;
          const HasBackground = KakaoResponse.HasBackground;

          Guard(KakaoProfile, TKakaoProfile.Create(KakaoResponse.ProfileJson));
          KakaoProfile.Profile.NickName:= ViewFriendInfo.Name;
          KakaoProfile.Profile.ScreenBase64:= ViewFriendInfo.ScreenToBase64;

          SetEventStatusAndPublish(EVENT_PROCESSING, Format(FRIEND_PROFILE_SCANNING, [ViewFriendInfo.Name]));
          SetProfileImageViewerType(rtViewTypeProfile);
          MergeFeeds(HasProfile, ViewFriendInfo.Handle, rtViewTypeProfile, KakaoProfile);

          SetEventStatusAndPublish(EVENT_PROCESSING, Format(FRIEND_BACKGROUND_SCANNING, [ViewFriendInfo.Name]));
          SetProfileImageViewerType(rtViewTypeBackground);
          MergeFeeds(HasBackground, ViewFriendInfo.Handle, rtViewTypeBackground, KakaoProfile);

          SetEventStatusAndPublish(EVENT_SUCCESS, KakaoProfile.ToJSON);
        end;
      finally
        Guard(EventStatus, Redis.GetEventStatus(EventId));
        LogMsg:= LogMsg + Format(#9'sec: %s'#10#9'result: %s', [FloatToStr((GetTickCount64 - StartTick) / 1000), EventStatus.Status]) + sLineBreak;
        Log(LogMsg);
        IsRunning:= False;
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
