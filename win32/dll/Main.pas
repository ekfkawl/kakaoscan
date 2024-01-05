unit Main;

interface

uses
  Winapi.Windows, System.Threading, System.Classes, System.SysUtils,
  KakaoHandle, KakaoCtrl, KakaoResponse, KakaoStatus, KakaoHook, KakaoProfile, RedisUtil, EventStatus, Test, GuardObjectUtil;

procedure Initialize;
procedure RunEvent(const EventId, PhoneNumber: string);

implementation

var
  IsRunning: Boolean;
  KakaoCtrl: TKakaoCtrl;
  Redis: TRedis;

procedure RunEvent(const EventId, PhoneNumber: string);
var
  KakaoResponse: TKakaoResponse;
  KakaoStatus: TKakaoStatus;
  KakaoProfile: TKakaoProfile;
  StatusResponse: TStatusResponse;
  ViewFriendName: string;
begin
  Redis.SetEventStatus(EventId, TEventStatus.CreateInstance(EVENT_PROCESSING, ''));

  if IsRunning then
    Exit;
  IsRunning:= True;

  TTask.Run(
    procedure
    begin
      try
        const Tick = GetTickCount64 + 1000;
        while (Tick > GetTickCount64) and (not Assigned(Redis.GetEventStatus(EventId))) do
          Sleep(1);

        if not KakaoCtrl.SearchFriend(PhoneNumber).Value then
        begin
          KakaoResponse:= KakaoCtrl.AddFriend(PhoneNumber).Value;
          KakaoCtrl.SynchronizationFriend;

          if KakaoResponse.ResponseType <> rtStatus then
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

        ViewFriendName:= KakaoCtrl.ViewFriend.Value;
        KakaoResponse:= GetRecentKakaoResponse;
        if KakaoResponse.ResponseType = rtProfile then
        begin
          Guard(KakaoProfile, TKakaoProfile.Create(KakaoResponse.Json));
          KakaoProfile.Profile.NickName:= ViewFriendName;
          Redis.SetEventStatus(EventId, TEventStatus.CreateInstance(EVENT_SUCCESS, KakaoProfile.ToJSON));

          WriteLn(#9'state: ', EVENT_SUCCESS);
        end;

      finally
        IsRunning:= False;
      end;
    end
  )
end;

procedure Initialize;
begin
  KakaoCtrl:= TKakaoCtrl.GetInstance;
  Redis:= TRedis.GetInstance;
end;

end.
