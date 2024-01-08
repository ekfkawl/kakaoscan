unit KakaoResponse;

interface

uses
  KakaoProfile, KakaoStatus;

type
  TResponseType = (rtProfile, rtStatus, rtFeeds, rtUnknown);

  TKakaoResponse = record
    ResponseType: TResponseType;
    HasProfile: boolean;
    HasBackground: boolean;
    Json: string;

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
