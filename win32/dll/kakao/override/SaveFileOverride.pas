unit SaveFileOverride;

interface

uses
  Winapi.Windows, System.StrUtils, System.SysUtils, System.IOUtils, CommDlg;

function CustomSaveFile(pOfn: POPENFILENAMEW): Integer; stdcall;

implementation

uses
  KakaoProfilePageUtil;

const
  EntrySavePath = 'C:\Kakao\';

function PathGetExtensionW(const S: string): string; inline;
begin
  Result:= ExtractFileExt(S);
end;

function PathChangeExtensionW(const FileName, Extension: string): string; inline;
begin
  Result:= ChangeFileExt(FileName, Extension);
end;

function PathGetFileNameW(const S: string): string; inline;
begin
  Result:= ExtractFileName(S);
end;

function PathGetDirectoryNameW(const S: string): string; inline;
begin
  Result:= ExcludeTrailingPathDelimiter(ExtractFilePath(S));
end;

function PathIsRootedW(const S: string): Boolean; inline;
begin
  Result:=
    (Length(S) >= 2) and (S[2] = ':') or
    ((Length(S) >= 2) and (S[1] = '\') and (S[2] = '\'));
end;

function PathGetFullPathW(const S: string): string;
begin
  if PathIsRootedW(S) then
    Result:= ExpandFileName(S)
  else
    Result:= ExpandFileName(IncludeTrailingPathDelimiter(GetCurrentDir) + S);
end;

function FirstExtFromPattern(const Pattern: UnicodeString): UnicodeString;
var
  p, q: Integer;
  one: UnicodeString;
begin
  Result:= '';
  if Pattern = '' then
    Exit;

  p:= Pos(';', Pattern);
  if p > 0 then
    one:= Copy(Pattern, 1, p - 1) else one:= Pattern;

  p:= Pos('*.', one);
  if p = 0 then
    Exit;

  q:= p + 2;
  if q <= Length(one) then
    Result:= '.' + Copy(one, q, MaxInt)
  else
    Result:= '';
end;

function GetFilterPatternByIndex(lpstrFilter: PWideChar; Index1Based: DWORD): UnicodeString;
var
  i: DWORD;
  cur: PWideChar;
  s: UnicodeString;
begin
  Result:= '';
  if (lpstrFilter = nil) or (Index1Based = 0) then
    Exit;

  cur:= lpstrFilter;
  i:= 0;
  while (cur^ <> #0) do
  begin
    s:= cur;
    Inc(cur, Length(s) + 1);
    if cur^ = #0 then
      break;

    Inc(i);
    s:= cur;
    Inc(cur, Length(s) + 1);

    if i = Index1Based then
    begin
      Result:= s;
      Exit;
    end;
  end;
end;

function ChooseAutoExtension(pOfn: POPENFILENAMEW): UnicodeString;
var
  ext: UnicodeString;
  fname: UnicodeString;
  patt: UnicodeString;
  idx: DWORD;
begin
  Result:= '';

  if (pOfn <> nil) and (pOfn.lpstrFile <> nil) and (pOfn.lpstrFile^ <> #0) then
  begin
    fname:= UnicodeString(pOfn.lpstrFile);
    ext:= PathGetExtensionW(fname);
    if ext <> '' then
      Exit(ext);
  end;

  if (pOfn <> nil) and (pOfn.lpstrFilter <> nil) then
  begin
    idx:= pOfn.nFilterIndex;
    if idx = 0 then
      idx:= 1;
    patt:= GetFilterPatternByIndex(pOfn.lpstrFilter, idx);
    ext:= FirstExtFromPattern(patt);
    if ext <> '' then
      Exit(ext);
  end;

  if (pOfn <> nil) and (pOfn.lpstrDefExt <> nil) and (pOfn.lpstrDefExt^ <> #0) then
  begin
    ext:= UnicodeString(pOfn.lpstrDefExt);
    if ext <> '' then
    begin
      if (ext[1] <> '.') then
        ext:= '.' + ext;
      Exit(ext);
    end;
  end;
end;

function PutPathIntoBufferW(const S: UnicodeString; Dest: PWideChar; DestCch: Cardinal): Boolean;
var need: Cardinal;
begin
  need:= Cardinal(Length(S) + 1);
  Result:= (Dest <> nil) and (DestCch >= need);
  if Result then
  begin
    Move(PWideChar(S)^, Dest^, Length(S)*SizeOf(WideChar));
    Dest[Length(S)]:= #0;
  end;
end;

function ComputeFileOffsetsW(const FullPath: UnicodeString; out FileOffset, ExtOffset: Word): Boolean;
var
  fname: UnicodeString;
  baseLen, dotPos: Integer;
begin
  Result:= False;
  fname:= PathGetFileNameW(FullPath);
  baseLen:= Length(FullPath) - Length(fname);
  if baseLen < 0 then
    Exit;
  FileOffset:= Word(baseLen);

  dotPos:= LastDelimiter('.', fname);
  if (dotPos > 0) and (dotPos < Length(fname)) then
    ExtOffset:= Word(baseLen + dotPos)
  else
    ExtOffset:= 0;

  Result:= True;
end;

function ReactivateOwner(hOwner: HWND): Boolean;
var
  ownerTid, selfTid: DWORD;
  attached: BOOL;
begin
  Result:= False;
  if (hOwner = 0) or (not IsWindow(hOwner)) then
    Exit;

  EnableWindow(hOwner, False);
  EnableWindow(hOwner, True);

  ownerTid:= GetWindowThreadProcessId(hOwner, nil);
  selfTid:= GetCurrentThreadId;
  attached:= AttachThreadInput(selfTid, ownerTid, True);
  try
    SetActiveWindow(hOwner);
    SetForegroundWindow(hOwner);
    SetFocus(hOwner);
  finally
    if attached then
      AttachThreadInput(selfTid, ownerTid, False);
  end;

  if GetCapture <> 0 then
    ReleaseCapture;

  SendMessageTimeout(hOwner, 0, 0, 0, SMTO_ABORTIFHUNG, 50, nil);

  Result:= True;
end;

function CustomSaveFile(pOfn: POPENFILENAMEW): Integer; stdcall;
var
  fullPath, dirPart, fileTitle, pickedExt: UnicodeString;
  fileOff, extOff: Word;
  hasExt: Boolean;
begin
  Result:= 0;

  ReactivateOwner(pOfn.hwndOwner);
  PostMessage(pOfn.hwndOwner, 0, 0, 0);

  if (pOfn = nil) then
  begin
    SetLastError(ERROR_INVALID_PARAMETER);
    Exit;
  end;

  fullPath:= EntrySavePath + GetProfilePage.Current.ToString + '-' + GetProfilePage.Last.ToString; //  GetTickCount64.ToString;
  if fullPath = '' then
  begin
    SetLastError(ERROR_CANCELLED);
    Exit;
  end;

  fullPath:= PathGetFullPathW(fullPath);
  pickedExt:= ChooseAutoExtension(pOfn);
  hasExt:= PathGetExtensionW(fullPath) <> '';

  if pickedExt <> '' then
  begin
    if hasExt then
      fullPath:= PathChangeExtensionW(fullPath, pickedExt)
    else
      fullPath:= fullPath + pickedExt;
  end;

  dirPart:= PathGetDirectoryNameW(fullPath);
  if (pOfn.Flags and OFN_PATHMUSTEXIST) <> 0 then
  begin
    if (dirPart = '') or (not DirectoryExists(dirPart)) then
    begin
      SetLastError(ERROR_PATH_NOT_FOUND);
      Exit;
    end;
  end
  else
  begin
    if (dirPart <> '') and (not DirectoryExists(dirPart)) then
      if not ForceDirectories(dirPart) then
      begin
        SetLastError(ERROR_PATH_NOT_FOUND);
        Exit;
      end;
  end;

  if (pOfn.Flags and OFN_OVERWRITEPROMPT) <> 0 then
  begin
    if FileExists(fullPath) then
    begin
      SetLastError(ERROR_FILE_EXISTS);
      Exit;
    end;
  end;

  if (pOfn.lpstrFile = nil) or (pOfn.nMaxFile = 0) then
  begin
    SetLastError(ERROR_INSUFFICIENT_BUFFER);
    Exit;
  end;
  if not PutPathIntoBufferW(fullPath, pOfn.lpstrFile, pOfn.nMaxFile) then
  begin
    SetLastError(ERROR_INSUFFICIENT_BUFFER);
    Exit;
  end;

  if (pOfn.lpstrFileTitle <> nil) and (pOfn.nMaxFileTitle > 0) then
  begin
    fileTitle:= PathGetFileNameW(fullPath);
    PutPathIntoBufferW(fileTitle, pOfn.lpstrFileTitle, pOfn.nMaxFileTitle);
  end;

  if not ComputeFileOffsetsW(fullPath, fileOff, extOff) then
  begin
    SetLastError(ERROR_INVALID_DATA);
    Exit;
  end;
  pOfn.nFileOffset:= fileOff;
  pOfn.nFileExtension:= extOff;

  Result:= 1;
end;

end.
