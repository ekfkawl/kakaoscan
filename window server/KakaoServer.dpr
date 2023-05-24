program KakaoServer;

uses
  Windows,
  Vcl.Forms,
  uKakaoServer in 'uKakaoServer.pas' {Form1},
  ClientInstance in 'ClientInstance.pas',
  SharableMemory in '..\common\SharableMemory.pas',
  KakaoAPI in 'KakaoAPI.pas',
  ProcessAPI in '..\common\ProcessAPI.pas',
  MD5 in '..\common\MD5.pas',
  HttpUtils in '..\common\HttpUtils.pas',
  uKey in '..\common\uKey.pas' {$R *.res},
  KafkaConst in '..\common\KafkaConst.pas';

{$R *.res}

begin
  CreateMutex(nil, False, '_savekakao_');
  if GetLastError = ERROR_ALREADY_EXISTS then
    Halt(0);

  Application.Initialize;
  Application.MainFormOnTaskbar := True;
  Application.CreateForm(TForm1, Form1);
  Application.Run;
end.
