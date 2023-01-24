unit uKakaoHook;

interface

uses
  Winapi.Windows, System.SysUtils, System.Classes, System.StrUtils, System.Types, KakaoUtils, MemAPI, SharableMemory, Vcl.Clipbrd, MD5,
  IdHttp, IdURI, IdSSL, IdSSLOpenSSL, uKey, HttpUtils, ActiveX, TlHelp32;

var
  Kakao: TKakao;

  pOriginSaveFriendName: function(pOriginName: Pointer): Pointer; stdcall;
  pOriginAddFriendSelectCountry: function: Pointer; stdcall;
  pSaveFile: procedure; stdcall;
  pNextProfile: procedure; stdcall;
  pOriginHttpRespon: function(pRespon: Pointer): Pointer; stdcall;
  pOriginSetBlockCount: procedure(BlockCount: Integer); stdcall;

  procedure Init;

implementation
  uses uKakaoHandle;

function UTF8ArrayToString(Arr: Pointer; Size: Cardinal): String;
var
  Res: AnsiString;
begin
  SetString(Res, PAnsiChar(Arr), Size);
  Result:= UTF8ToAnsi(Res);
end;

function SaveFriendName(pOriginName: Pointer): Pointer; stdcall;
  procedure f(pOriginName: Pointer); stdcall;
  var
    OriginName, CustomName: PWideChar;
    DecodeOriginName: UTF8String;
    WideOriginName: WideString;
  begin
    // string pointer
    OriginName:= PPointer(DWORD(pOriginName) - $14)^;
    CustomName:= PPointer(DWORD(pOriginName) - 4)^;

    DecodeOriginName:= UTF8ArrayToString(OriginName, PDWORD(DWORD(OriginName) - 8)^);
    WideOriginName:= UTF8ToWideString(DecodeOriginName);

    ZeroMemory(@Kakao.SharableInstance.FriendOriginName, SizeOf(Kakao.SharableInstance.FriendOriginName));
    ZeroMemory(@Kakao.SharableInstance.FriendCustomName, SizeOf(Kakao.SharableInstance.FriendCustomName));

    if Assigned(OriginName) And Assigned(CustomName) then
    begin
      // SharableMemory에 OriginName, CustomName 복사
      CopyMemory(@Kakao.SharableInstance.FriendOriginName, PDWORD(WideOriginName), Length(WideOriginName) * 2 + 2);
      CopyMemory(@Kakao.SharableInstance.FriendCustomName, CustomName, Length(CustomName) * 2 + 2);

//      Writeln(WideOriginName);
//      Writeln(Kakao.SharableInstance.FriendCustomName);
    end;
  end;
begin
  asm
    pushad

    push pOriginName
    call f

    popad
  end;
  Result:= pOriginSaveFriendName(pOriginName);
end;

function AddFriendSelectCountry: Pointer; stdcall;
asm
  mov [edi+$378], $CC // +82
  call pOriginAddFriendSelectCountry
end;

procedure GetSearchCount; stdcall;
  procedure f(SearchCount: Pointer); stdcall;
  begin
    Kakao.SharableInstance.SearchCount:= PInteger(SearchCount)^;
    Kakao.SharableInstance.pSearchCount:= SearchCount;
  end;
asm
  mov ecx, [esi+$258]
  pushad
  lea eax, [ecx+8] // pCount
  push eax
  call f
  popad
end;

procedure SaveFile; stdcall;
asm
  pushad
  test ecx, ecx
  je @End
  call pSaveFile
  @End:
  popad
end;

procedure SaveFileEndPoint;
asm
  mov ecx,[ebp-$0C]
  mov fs:[0], ecx
  pop ecx
  pop edi
  pop esi
  pop ebx
  mov esp,ebp
  pop ebp
  ret
end;

function SaveFileCustom(OpenFile: DWORD): Integer; stdcall;
  function f(OpenFile: DWORD): Integer; stdcall;
  var
    OldName: PWideString;
    BaseDir, NewName: WideString;
    Source: String;
    ImagePage: TArray<String>;
  begin
    NewName:= '';
    try
      try
        BaseDir:= ROOT + StrToMD5(AnsiString(Kakao.SharableInstance.FriendCustomName));

        Source:= String(Kakao.SharableInstance.FriendCustomName) + ' ';
        ViewProfileHandle:= 0;
        EnumWindows(@GetViewProfileHandleCallback, DWORD(Source));

        if ViewProfileHandle > 0 then
        begin
          // 페이징 정보 구함
          ImagePage:= GetWindowCaption(ViewProfileHandle).Split([' ', '/']);
          if Length(ImagePage) = 3 then
          begin
            const CurrentPage = ImagePage[1].ToInteger;

            // max save image count
            if CurrentPage > IMG_MAX_DOWNLOAD_COUNT then
            begin
              Exit;
            end;

            // 저장 가능한 파일이면 저장
            OldName:= PPointer(OpenFile + $1C)^;
            if Length(PWideChar(OldName)) > 0 then
            begin
              // local save
              var Path:= IIS_PROFILE_PATH;
              if Kakao.SharableInstance.gSaveStep = 2 then
                Path:= IIS_BG_PATH;

              NewName:= Format('%s\%s\%s.mp4', [BaseDir, Path, ImagePage[1]]);

              CopyMemory(PPointer(OpenFile + $1C)^, PDWORD(NewName), 200);
            end;

          end;
        end;
      except;
      end;
    finally
      if Length(NewName) > 0 then
        Result:= 1
      else
        Result:= 0;
    end;
  end;
asm
  push ebx
  push ecx
  push edx
  push edi
  push esi

  push OpenFile
  call f

  pop esi
  pop edi
  pop edx
  pop ecx
  pop ebx
end;

function GetJSONValue(Key, Source: String): String;
begin
  Result:= Source.Split([Format('"%s":"', [Key])])[1].Split(['"'])[0];
end;

procedure UpdateSharableJSONData(Dest: Pointer; Key, Source: String);
var
  s: String;
begin
  s:= GetJSONValue(Key, Source);
  CopyMemory(Dest, PDWORD(s), Length(s) * 2 + 2);
end;

function HttpResponHijack(pRespon: Pointer): Pointer; stdcall;
  //
  procedure f(pRespon: Pointer); stdcall;
  var
    pHttpRespon: ^UTF8String;
    HttpRespon: String;
  begin
    try
      if PByte(pRespon)^ = 0 then
        Exit;

      pHttpRespon:= PPointer(PDWORD(pRespon)^ + 4)^;
      HttpRespon:= UTF8ArrayToString(pHttpRespon, PDWORD(PDWORD(pRespon)^ + $80)^ + 1{size});

      Kakao.SharableInstance.AddFriendResult:= ADD_SUCCESS;

      // 유저 정보
      if Pos('"originalProfileImageUrl":"', HttpRespon) > 0 then
      begin
        Kakao.SharableInstance.EmptyProfileImage:= False;
        Kakao.SharableInstance.EmptyBackgroundImage:= False;

        ZeroMemory(@Kakao.SharableInstance.StatusMessage, SizeOf(Kakao.SharableInstance.StatusMessage));
        ZeroMemory(@Kakao.SharableInstance.ProfileImageUrl, SizeOf(Kakao.SharableInstance.ProfileImageUrl));
        ZeroMemory(@Kakao.SharableInstance.MusicName, SizeOf(Kakao.SharableInstance.MusicName));
        ZeroMemory(@Kakao.SharableInstance.ArtistName, SizeOf(Kakao.SharableInstance.ArtistName));
        ZeroMemory(@Kakao.SharableInstance.MusicAlbumUrl, SizeOf(Kakao.SharableInstance.MusicAlbumUrl));
        //

        Kakao.SharableInstance.EmptyProfileImage:= Pos('"originalProfileImageUrl":""', HttpRespon) > 0;

        Kakao.SharableInstance.EmptyBackgroundImage:= Pos('"originalBackgroundImageUrl":""', HttpRespon) > 0;

        // 현재 프로필
        if not Kakao.SharableInstance.EmptyProfileImage then
        begin
          UpdateSharableJSONData(@Kakao.SharableInstance.ProfileImageUrl, 'profileImageUrl', HttpRespon);
        end;

        // 상메
        if Pos(',"statusMessage":""', HttpRespon) = 0 then
        begin
          UpdateSharableJSONData(@Kakao.SharableInstance.StatusMessage, 'statusMessage', HttpRespon);
        end;

        // 뮤직
        if Pos(',"musics":{"total":', HttpRespon) > 0 then
        begin
          UpdateSharableJSONData(@Kakao.SharableInstance.MusicName, 'contentName', HttpRespon);

          UpdateSharableJSONData(@Kakao.SharableInstance.MusicAlbumUrl, 'contentImgPath', HttpRespon);

          UpdateSharableJSONData(@Kakao.SharableInstance.ArtistName, 'artistName', HttpRespon);
        end;
      end
      else begin
        // 친추 결과
        if Pos('"suspended":true', HttpRespon) > 0 then
        begin
          Kakao.SharableInstance.AddFriendResult:= ADD_BAN_USER;
        end
        else begin
          if Pos('{"status":-500,"message":"', HttpRespon) > 0 then
          begin
            const AddFriendResult = GetJSONValue('message', HttpRespon);

            if Pos('입력하신 번호는 이미 등록된 친구입니다.', AddFriendResult) > 0 then
            begin
              Kakao.SharableInstance.AddFriendResult:= ADD_ALREADY;
            end
            else if Pos('유효하지 않은 전화번호입니다.', AddFriendResult) > 0 then
            begin
              Kakao.SharableInstance.AddFriendResult:= ADD_FAIL;

              HttpPost(Format('/cache?phoneNumber=%s&enabled=false&key=%s', [Kakao.SharableInstance.GetRequestPhoneNumber, HTTP_KEY]));
            end;
          end;
        end;
      end;

    except;
    end;
  end;
  //`
asm
  pushad

  push pRespon
  call f

  popad

  push pRespon
  call pOriginHttpRespon;
end;

procedure GetBlockCount(BlockCount: Integer); stdcall;
  procedure f(pBlockCount: Pointer); stdcall;
  begin
    Kakao.SharableInstance.pBlockCount:= pBlockCount;
  end;
asm
  pushad

  mov eax, [ebx+$234]
  add eax, 8
  push eax
  call f

  popad

  push BlockCount
  call pOriginSetBlockCount;
end;

procedure Init;
begin
  Kakao:= TKakao.Create;

  // 후킹 작업
  if Kakao.IsInjectedKakaoTalk then
  begin

//    Allocconsole;

    // 프로필 로드시 이름(원본, 커스텀) 가져옴
//    WriteProtectMemory1(Kakao.HookFriendName1 + $A, $85);
    WriteProtectMemory2(Kakao.HookFriendName2 - 9, $9090);

    pOriginSaveFriendName:= Ptr(GetCallPtr(Kakao.HookFriendName2));
    CallHook(Kakao.HookFriendName2, @SaveFriendName);

    // 친구 추가시 자동으로 국가 선택
    pOriginAddFriendSelectCountry:= Ptr(GetCallPtr(Kakao.HookAddFriendSelectCountry));
    CallHook(Kakao.HookAddFriendSelectCountry, @AddFriendSelectCountry);

    // HTTP 응답 하이재킹
    pOriginHttpRespon:= Ptr(GetCallPtr(Kakao.HookHttpRespon));
    CallHook(Kakao.HookHttpRespon, @HttpResponHijack);

    // 친구 검색 아이템 카운트
    CallHook(Kakao.HookSearchCount, @GetSearchCount);
    WriteProtectMemory1(Kakao.HookSearchCount + 5, $90); // nop

    // 로컬
    WriteProtectMemory4(Kakao.HookIsLocal, $90909090);
    WriteProtectMemory2(Kakao.HookIsLocal + 4, $9090);

    // Save
    pSaveFile:= Ptr(Kakao.FuncSaveFile);
    CallHook(Kakao.HookSaveFile - $17B, @SaveFile);
    JumpHook(Kakao.HookSaveFile - $17B + 5, @SaveFileEndPoint);

    // Save2
    CallHook(Kakao.HookSaveFileCustom, @SaveFileCustom);
    WriteProtectMemory1(Kakao.HookSaveFileCustom + 5, $90); // nop

    // 프로필 next
    pNextProfile:= Ptr(Kakao.FuncNextProfile);

    // 친구 차단 리스트박스 카운트
    pOriginSetBlockCount:= Ptr(GetCallPtr(Kakao.HookBlockCount));
    CallHook(Kakao.HookBlockCount, @GetBlockCount);

    // 친구 동기화 60초 제한 해제
    JumpHook(Kakao.HookSyncFriend, Kakao.HookSyncFriend + $1A3);

  end;
end;

end.
