unit KakaoResponse;

interface

uses
  KakaoProfile, KakaoFriend;

type
  TResponseType = (rtProfile, rtStatus, rtFeeds, rtFriend, rtUnknown);

  TKakaoResponse = record
    ResponseType: TResponseType;
    HasProfile: boolean;
    HasBackground: boolean;
    Json: string;
    CalledTick: UInt64;

    class function Initialize: TKakaoResponse; static;
  end;

implementation

class function TKakaoResponse.Initialize: TKakaoResponse;
begin
  Result.ResponseType:= rtUnknown;
  Result.HasProfile:= False;
  Result.HasBackground:= False;
  Result.Json:= '';
end;

end.
