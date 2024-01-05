unit KakaoResponse;

interface

uses
  KakaoProfile, KakaoStatus;

type
  TResponseType = (rtProfile, rtStatus, rtUnknown);

  TKakaoResponse = record
    ResponseType: TResponseType;
    Json: string;
  end;

implementation

end.
