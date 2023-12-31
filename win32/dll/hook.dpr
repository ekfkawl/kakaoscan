library hook;

uses
  Winapi.Windows,
  System.SysUtils,
  System.Classes,
  LogUtil in '..\common\LogUtil.pas',
  RedisUtil in 'utils\RedisUtil.pas',
  RedisConfig in 'config\RedisConfig.pas',
  RedisSearchEventHandler in 'event\handler\RedisSearchEventHandler.pas',
  GuardObjectUtil in '..\common\GuardObjectUtil.pas',
  EventMetadata in 'event\model\EventMetadata.pas',
  SearchEvent in 'event\model\SearchEvent.pas',
  EventStatus in 'event\model\EventStatus.pas',
  AOBScanUtil in 'utils\AOBScanUtil.pas';

{$R *.res}

begin
  AllocConsole;
end.
