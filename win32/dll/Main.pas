unit Main;

interface

uses
  Winapi.Windows, System.Threading, System.Classes, System.SysUtils,
  KakaoHandle, KakaoCtrl, KakaoResponse, KakaoStatus, KakaoId, KakaoHook, KakaoProfile, RedisUtil, SearchEvent, EventStatus, Test, GuardObjectUtil,
  InvalidPhoneNumber, SearchNewPhoneNumberEvent, RedisConfig, KakaoEnumCallback;

procedure Initialize;
procedure RunEvent(const EventId, Email, PhoneNumber: string; IsId: boolean);

implementation

var
  IsRunning: Boolean;
  KakaoCtrl: TKakaoCtrl;
  Redis: TRedis;

procedure MergeFeeds(HasFeeds: Boolean; hViewFriendWindow: THandle; ScanType: Integer; var KakaoProfile: TKakaoProfile);
var
  FeedsContainer: TFeedsContainer;
begin
  if HasFeeds then
  begin
    Guard(FeedsContainer, KakaoCtrl.Scan(hViewFriendWindow, ScanType).Value);
    if Assigned(FeedsContainer) then
    begin
      if ScanType = 0 then
        KakaoProfile.Profile.ProfileFeeds.Merge(FeedsContainer)
      else
        KakaoProfile.Profile.BackgroundFeeds.Merge(FeedsContainer);

      CleanUpMemory;
    end;
  end;
end;

procedure RunEvent(const EventId, Email, PhoneNumber: string; IsId: boolean);
var
  KakaoResponse: TKakaoResponse;
  KakaoStatus: TKakaoStatus;
  KakaoQueryResult: TKakaoQueryResult;
  OpenIdResult: TOpenIdResult;
  SearchNewNumberEvent: TSearchNewPhoneNumberEvent;
  SearchEvent: TSearchEvent;
  KakaoProfile: TKakaoProfile;
  StatusResponse: TStatusResponse;
  ViewFriendInfo: TViewFriendInfo;
  EventStatus: TEventStatus;
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
      EnumWindows(@CloseDialogWindow, 0);

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
          if KakaoResponse.ResponseType <> rtFriend then
          begin
            SetEventStatusAndPublish(EVENT_FAILURE, FAILURE_ADD_FRIEND);
            Exit;
          end;

          KakaoCtrl.SynchronizationFriend;

          Guard(KakaoStatus, TKakaoStatus.Create(KakaoResponse.Json));
          StatusResponse:= KakaoStatus.GetStatusResponse;

          if KakaoStatus.Status = STATUS_FAILURE then
          begin
            SetEventStatusAndPublish(EVENT_FAILURE, StatusResponse.Message);

            if StatusResponse.Message = INVALID_PHONE_NUMBER then
            begin
              Redis.CacheInvalidPhoneNumber(PhoneNumber, TInvalidPhoneNumber.CreateInstance(Email));
              Writeln(#9'isInvalid: TRUE');
            end;

            Exit;
          end
          else if KakaoStatus.Status = STATUS_OK then
          begin
            Guard(SearchNewNumberEvent, TSearchNewPhoneNumberEvent.Create(Email, PhoneNumber));
            Redis.Publish(OTHER_EVENT_TOPIC, SearchNewNumberEvent.ToEventJSON);
            Writeln(#9'isNewPhoneNumber: TRUE');

            if not KakaoCtrl.SearchFriend(PhoneNumber).Value then
            begin
              SetEventStatusAndPublish(EVENT_FAILURE, DELAYED_FRIEND_SYNC);
              Exit;
            end;
          end;
        end
        else if IsId then
        begin
          KakaoResponse:= KakaoCtrl.AddFriend(PhoneNumber, rtId).Value;
          if KakaoResponse.ResponseType <> rtQuery then
          begin
            SetEventStatusAndPublish(EVENT_FAILURE, FAILURE_ADD_FRIEND);
            Exit;
          end;

          Guard(KakaoQueryResult, TKakaoQueryResult.Create(KakaoResponse.Json));
          if not KakaoQueryResult.IsPresent then
          begin
            SetEventStatusAndPublish(EVENT_FAILURE, INVALID_KAKAO_ID);
            Redis.CacheInvalidPhoneNumber(PhoneNumber, TInvalidPhoneNumber.CreateInstance(Email));
            Writeln(#9'isInvalid: TRUE');
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
            Writeln(#9'isNewKakaoId: TRUE');
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

          Guard(KakaoProfile, TKakaoProfile.Create(KakaoResponse.Json));
          KakaoProfile.Profile.NickName:= ViewFriendInfo.Name;
          KakaoProfile.Profile.ScreenBase64:= ViewFriendInfo.ScreenToBase64;

          SetEventStatusAndPublish(EVENT_PROCESSING, Format(FRIEND_PROFILE_SCANNING, [ViewFriendInfo.Name]));
          MergeFeeds(HasProfile, ViewFriendInfo.Handle, 0, KakaoProfile);

          SetEventStatusAndPublish(EVENT_PROCESSING, Format(FRIEND_BACKGROUND_SCANNING, [ViewFriendInfo.Name]));
          MergeFeeds(HasBackground, ViewFriendInfo.Handle, 1, KakaoProfile);

          SetEventStatusAndPublish(EVENT_SUCCESS, KakaoProfile.ToJSON);
        end;
      finally
        Guard(EventStatus, Redis.GetEventStatus(EventId));
        Writeln(Format(#9'sec: %s'#10#9'result: %s', [FloatToStr((GetTickCount64 - StartTick) / 1000), EventStatus.Status]));
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
