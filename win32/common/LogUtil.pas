unit LogUtil;

interface

uses
  System.SysUtils, System.IOUtils;

procedure Log(const Msg: string; E: Exception);

implementation

const LOG_PATH = 'C:\kakaoscan.log';

procedure Log(const Msg: string; E: Exception);
var
  LogFile: TextFile;
  FileName: string;
begin
  FileName:= LOG_PATH;
  AssignFile(LogFile, FileName);
  try
    if not FileExists(FileName) then
      Rewrite(LogFile)
    else
      Append(LogFile);

    WriteLn(LogFile, Format('[%s] %s: %s %s: %s', [FormatDateTime('hh:mm:ss', Now), E.ClassName, #13#10, Msg, E.Message]));
  finally
    CloseFile(LogFile);
  end;
end;

end.
