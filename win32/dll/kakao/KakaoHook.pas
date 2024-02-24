unit KakaoHook;

interface

uses
  Winapi.Windows, System.StrUtils, System.SysUtils, Clipbrd, System.Hash,
  KakaoSignature, AOBScanUtil, MemoryUtil, LogUtil, KakaoProfile, KakaoStatus, KakaoResponse, GuardObjectUtil;

function GetSearchCount: DWORD;
function GetRecentKakaoResponse: TKakaoResponse;
function GetRecentFriendViewName: string;
function GetProfileStructure: DWORD;
function GetMoreProfile: TFeedsContainer;
procedure CleanUpMemory;

implementation

var
  Scanner: TAOBScanner;
  SearchCount, SearchCountRetn,
  ProfileStructure, LoadProfileRetn: DWORD;

  KakaoResponse: TKakaoResponse;
  ViewFriendName: string;

  DynamicFeedsContainer: TDynamicFeedsContainer;

  pOriginHttpResponse: function(pResponse: Pointer): Pointer; stdcall;
  pOriginFriendName: function(pName: Pointer): Pointer; stdcall;

function UTF8ArrayToString(pStr: Pointer; Size: Cardinal): string;
var
  Res: AnsiString;
begin
  SetString(Res, PAnsiChar(pStr), Size);
  Result:= UTF8ToAnsi(Res);
end;

procedure SearchCountHook;
asm
  mov ecx, [esi+$258]
  mov eax, [ecx+8]
  mov [SearchCount], eax
  test eax, eax
  jmp dword ptr [SearchCountRetn]
end;

function GetSearchCount: DWORD;
begin
  Result:= SearchCount;
end;

function GetRecentKakaoResponse: TKakaoResponse;
var
  Res: TKakaoResponse;
begin
  Res.ResponseType:= KakaoResponse.ResponseType;
  Res.Json:= KakaoResponse.Json;
  Res.HasProfile:= KakaoResponse.HasProfile;
  Res.HasBackground:= KakaoResponse.HasBackground;
  Result:= Res;
end;

function GetRecentFriendViewName: string;
begin
  Result:= ViewFriendName;
end;

function GetProfileStructure: DWORD;
begin
  Result:= ProfileStructure;
end;

function GetMoreProfile: TFeedsContainer;
var
  Res: TFeedsContainer;
begin
  Result:= nil;
  if DynamicFeedsContainer.HashIndex.Count > 1 then
  begin
    Res:= TFeedsContainer.Create(DynamicFeedsContainer.Get(0).ToJSON);
    try
      for var i:= 0 to DynamicFeedsContainer.HashIndex.Count - 1 do
      begin
        Res.Merge(DynamicFeedsContainer.Get(i));
      end;
      Result:= Res;
    except
      Res.Free;
      raise;
    end;
  end;
end;

procedure CleanUpMemory;
begin
  ProfileStructure:= 0;
  DynamicFeedsContainer.Clear;
end;

function HttpResponseHook(pResponse: Pointer): Pointer; stdcall;
  procedure _HttpResponseHook(pResponse: Pointer); stdcall;
  var
    pJson: ^UTF8String;
    Json: string;
    KakaoProfile: TKakaoProfile;
    KakaoStatus: TKakaoStatus;
    FeedsContainer: TFeedsContainer;
  begin
    try
      if not Assigned(PPointer(pResponse)^) then
        Exit;

      pJson:= PPointer(PDWORD(pResponse)^ + 4)^;
      if not Assigned(pJson) then
        Exit;

      const JsonSize = PDWORD(PDWORD(pResponse)^ + $38)^;
      if JsonSize = 0 then
        Exit;

      Json:= UTF8ArrayToString(pJson, JsonSize);
      const IsProfile = PDWORD64(pJson)^ = $6C69666F7270227B;
      const IsStatus = PDWORD64(pJson)^ = $737574617473227B;
      const IsFeeds = PDWORD64(pJson)^ = $227364656566227B;
      const IsFriend = PDWORD64(pJson)^ = $646E65697266227B;

      KakaoResponse:= TKakaoResponse.Initialize;
      if IsProfile then
      begin
        Guard(KakaoProfile, TKakaoProfile.Create(Json));
        if not Assigned(KakaoProfile.Profile) then
          Exit;

        KakaoResponse.HasProfile:= KakaoProfile.Profile.IsHasProfile;
        KakaoResponse.HasBackground:= KakaoProfile.Profile.IsHasBackground;
        CleanUpMemory;

        KakaoResponse.ResponseType:= rtProfile;
      end
      else if IsStatus then
      begin
        Guard(KakaoStatus, TKakaoStatus.Create(Json));
        if not Assigned(KakaoStatus) then
          Exit;

        KakaoResponse.ResponseType:= rtStatus;
      end
      else if IsFeeds then
      begin
        FeedsContainer:= TFeedsContainer.Create(Json);
        DynamicFeedsContainer.Add(FeedsContainer.Feeds[0].Id, FeedsContainer);

        KakaoResponse.ResponseType:= rtFeeds;
      end
      else if IsFriend then
      begin
        KakaoResponse.ResponseType:= rtFriend;
      end;

      KakaoResponse.Json:= Json;
    except
      on E: Exception do
      begin
        Log('HttpResponseHook error' + #13#10 + Json, E);
      end;
    end;
  end;
asm
  pushad
  push pResponse
  call _HttpResponseHook
  popad

  push pResponse
  call dword ptr [pOriginHttpResponse]
end;

function FriendNameHook(pName: Pointer): Pointer; stdcall;
  procedure _FriendNameHook(pName: Pointer); stdcall;
  var
    pOriginName: PWideChar;
    OriginName: string;
  begin
    pOriginName:= PPointer(DWORD(pName) - $14)^;
    const OriginNameSize = PDWORD(DWORD(pOriginName) - 8)^;
    OriginName:= UTF8ArrayToString(pOriginName, OriginNameSize);
    ViewFriendName:= OriginName;
  end;
asm
  pushad
  push pName
  call _FriendNameHook
  popad

  push pName
  call dword ptr [pOriginFriendName]
end;

procedure LoadProfileHook;
asm
  mov [ebp-4], 0
  mov [ProfileStructure], ecx
  jmp dword ptr [LoadProfileRetn]
end;

initialization
  DynamicFeedsContainer:= TDynamicFeedsContainer.Create;

  Scanner:= TAOBScanner.GetInstance;
  Scanner.UpdateScanStructure(GetModuleHandle(nil), GetModuleHandle(nil) * 2);

  with Scanner do
  begin
    AOBSCAN(SIG_GET_FRIEND_NAME1, 0, procedure(Address: DWORD)
    begin
      WriteProtectedMemory1(Address + 8, 1);
    end);

    AOBSCAN(SIG_GET_FRIEND_NAME2, 0, procedure(Address: DWORD)
    begin
      WriteProtectedMemory2(Address - 2, $9090);
      pOriginFriendName:= Ptr(GetCallAddress(Address + 7));
      CallHook(Address + 7, @FriendNameHook);
    end);

    AOBSCAN(SIG_GET_SEARCH_COUNT, 0, procedure(Address: DWORD)
    begin
      SearchCountRetn:= Address + $C;
      JumpHook(Address + 2, @SearchCountHook);
    end);

    AOBSCAN(SIG_GET_HTTP_RESPONSE, 1, procedure(Address: DWORD)
    begin
      pOriginHttpResponse:= Ptr(GetCallAddress(Address + $A));
      CallHook(Address + $A, @HttpResponseHook);
    end);

    AOBSCAN(SIG_LOAD_PROFILE, 0, procedure(Address: DWORD)
    begin
      LoadProfileRetn:= Address + 7;
      JumpHook(Address, @LoadProfileHook);
    end);
  end;

finalization
  DynamicFeedsContainer.Free;

end.
