unit StackTraceUtil;

interface

uses
  SysUtils, Classes, JclDebug;

function StackTrace(PInfo: Pointer): String;

implementation

function GetExceptionStackInfoProc(P: PExceptionRecord): Pointer;
var
  LLines: TStringList;
  LText: String;
  LResult: PChar;
begin
  LLines := TStringList.Create;
  try
    JclLastExceptStackListToStrings(LLines, True, True, True, True);
    LText := LLines.Text;
    LResult := StrAlloc(Length(LText));
    StrCopy(LResult, PChar(LText));
    Result := LResult;
  finally
    LLines.Free;
  end;
end;

function GetStackInfoStringProc(Info: Pointer): String;
begin
  Result := String(PChar(Info));
end;

procedure CleanUpStackInfoProc(Info: Pointer);
begin
  StrDispose(PChar(Info));
end;

function StackTrace(PInfo: Pointer): String;
begin
  {$IFDEF DEBUG}
  Result:= GetStackInfoStringProc(PInfo);
  {$ELSE}
  Result:= String.Empty;
  {$ENDIF}
end;

initialization
  // Start the Jcl exception tracking and register our Exception
  // stack trace provider.
  {$IFDEF DEBUG}
  if JclStartExceptionTracking then
  begin
    Exception.GetExceptionStackInfoProc := GetExceptionStackInfoProc;
    Exception.GetStackInfoStringProc := GetStackInfoStringProc;
    Exception.CleanUpStackInfoProc := CleanUpStackInfoProc;
  end;
  {$ENDIF}

finalization
  // Stop Jcl exception tracking and unregister our provider.
  {$IFDEF DEBUG}
  if JclExceptionTrackingActive then
  begin
    Exception.GetExceptionStackInfoProc := nil;
    Exception.GetStackInfoStringProc := nil;
    Exception.CleanUpStackInfoProc := nil;
    JclStopExceptionTracking;
  end;
  {$ENDIF}

end.
