library hook;

uses
  Winapi.Windows,
  System.SysUtils,
  System.Classes,
  System.Threading,
  RedisUtils in 'utils\RedisUtils.pas',
  RedisConfig in 'config\RedisConfig.pas',
  RedisSearchEventHandler in 'event\handler\RedisSearchEventHandler.pas',
  EventMetadata in 'event\model\EventMetadata.pas',
  SearchEvent in 'event\model\SearchEvent.pas',
  EventStatus in 'event\model\EventStatus.pas',
  AOBScanUtils in 'utils\AOBScanUtils.pas',
  KakaoHandle in 'kakao\KakaoHandle.pas',
  Main in 'Main.pas',
  KakaoSignature in 'kakao\private\KakaoSignature.pas',
  MemoryUtils in 'utils\MemoryUtils.pas',
  Test in 'Test.pas' {Form1},
  KakaoProfile in 'kakao\private\model\KakaoProfile.pas',
  KakaoEnumCallback in 'kakao\private\KakaoEnumCallback.pas',
  BitmapUtils in 'utils\BitmapUtils.pas',
  KakaoProfilePageUtil in 'kakao\private\KakaoProfilePageUtil.pas',
  KakaoResponse in 'kakao\model\KakaoResponse.pas',
  InvalidPhoneNumber in 'search\model\InvalidPhoneNumber.pas',
  SearchNewPhoneNumberEvent in 'event\model\SearchNewPhoneNumberEvent.pas',
  KakaoHook in 'kakao\private\KakaoHook.pas',
  Unlink in 'Unlink.pas',
  KakaoParent in 'kakao\private\model\KakaoParent.pas',
  KakaoId in 'kakao\private\model\KakaoId.pas',
  KakaoFriend in 'kakao\private\model\KakaoFriend.pas',
  KakaoCtrl in 'kakao\private\KakaoCtrl.pas',
  SaveFileOverride in 'kakao\override\SaveFileOverride.pas',
  GuardObjectUtils in 'utils\GuardObjectUtils.pas',
  LogUtils in 'utils\LogUtils.pas';

{$R *.res}

begin
  HideModule(HInstance);
  AllocConsole;
  Main.Initialize;
end.
