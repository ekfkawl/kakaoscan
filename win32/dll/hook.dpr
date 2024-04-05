library hook;

uses
  Winapi.Windows,
  System.SysUtils,
  System.Classes,
  System.Threading,
  LogUtil in '..\common\LogUtil.pas',
  RedisUtil in 'utils\RedisUtil.pas',
  RedisConfig in 'config\RedisConfig.pas',
  RedisSearchEventHandler in 'event\handler\RedisSearchEventHandler.pas',
  GuardObjectUtil in '..\common\GuardObjectUtil.pas',
  EventMetadata in 'event\model\EventMetadata.pas',
  SearchEvent in 'event\model\SearchEvent.pas',
  EventStatus in 'event\model\EventStatus.pas',
  AOBScanUtil in 'utils\AOBScanUtil.pas',
  KakaoHandle in 'kakao\KakaoHandle.pas',
  Main in 'Main.pas',
  KakaoHook in 'kakao\KakaoHook.pas',
  KakaoSignature in 'kakao\private\KakaoSignature.pas',
  MemoryUtil in 'utils\MemoryUtil.pas',
  Test in 'Test.pas' {Form1},
  KakaoCtrl in 'kakao\KakaoCtrl.pas',
  KakaoProfile in 'kakao\private\model\KakaoProfile.pas',
  KakaoStatus in 'kakao\private\model\KakaoStatus.pas',
  KakaoEnumCallback in 'kakao\private\KakaoEnumCallback.pas',
  BitmapUtil in 'utils\BitmapUtil.pas',
  KakaoProfilePageUtil in 'kakao\private\KakaoProfilePageUtil.pas',
  KakaoResponse in 'kakao\model\KakaoResponse.pas',
  InvalidPhoneNumber in 'search\model\InvalidPhoneNumber.pas',
  SearchNewPhoneNumberEvent in 'event\model\SearchNewPhoneNumberEvent.pas';

{$R *.res}

begin
  AllocConsole;
  Main.Initialize;
end.
