unit LogUtil;

interface

uses
  System.SysUtils, System.IOUtils;

procedure Log(const Msg: string; E: Exception); overload;
procedure Log(const Msg: string); overload;

implementation

const LOG_PATH = 'C:\kakaoscan.log';

procedure Log(const Msg: string; E: Exception);
var
  LogFile: TextFile;
  FileName, Log: string;
begin
  FileName:= LOG_PATH;
  AssignFile(LogFile, FileName);
  try
    if not FileExists(FileName) then
      Rewrite(LogFile)
    else
      Append(LogFile);

    Log:= Format('[%s] %s: %s %s: %s', [FormatDateTime('hh:mm:ss', Now), E.ClassName, #13#10, Msg, E.Message]);
    WriteLn(LogFile, Log);
    WriteLn(Log);
  finally
    CloseFile(LogFile);
  end;
end;

procedure Log(const Msg: string);
var
  LogFile: TextFile;
  FileName, Log: string;
begin
  FileName:= LOG_PATH;
  AssignFile(LogFile, FileName);
  try
    if not FileExists(FileName) then
      Rewrite(LogFile)
    else
      Append(LogFile);

    Log:= Format('[%s] %s', [FormatDateTime('hh:mm:ss', Now), Msg]);
    WriteLn(LogFile, Log);
    WriteLn(Log);
  finally
    CloseFile(LogFile);
  end;
end;

end.
