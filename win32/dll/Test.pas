unit Test;

interface

uses
  Winapi.Windows, Winapi.Messages, System.SysUtils, System.Variants, System.Classes, Vcl.Graphics,
  Vcl.Controls, Vcl.Forms, Vcl.Dialogs, Vcl.StdCtrls, KakaoCtrl, System.Threading, KakaoHook,
  KakaoResponse, Vcl.ExtCtrls, KakaoProfilePageUtil, KakaoProfile, GuardObjectUtil, RedisUtil, RedisConfig,
  SearchNewPhoneNumberEvent;

type
  TForm1 = class(TForm)
    Button1: TButton;
    Edit1: TEdit;
    Button2: TButton;
    Button3: TButton;
    Button4: TButton;
    Timer1: TTimer;
    CheckBox1: TCheckBox;
    Button5: TButton;
    Button6: TButton;
    procedure FormCreate(Sender: TObject);
    procedure Button1Click(Sender: TObject);
    procedure Button2Click(Sender: TObject);
    procedure Button3Click(Sender: TObject);
    procedure Button4Click(Sender: TObject);
    procedure CheckBox1Click(Sender: TObject);
    procedure Timer1Timer(Sender: TObject);
    procedure Button5Click(Sender: TObject);
    procedure FormClose(Sender: TObject; var Action: TCloseAction);
    procedure Button6Click(Sender: TObject);
  private
    { Private declarations }
  public
    { Public declarations }
  end;

var
  Form1: TForm1;
  KakaoCtrl: TKakaoCtrl;
  Redis: TRedis;

implementation

{$R *.dfm}

procedure TForm1.Button1Click(Sender: TObject);
var
  Future: IFuture<boolean>;
begin
  Future:= KakaoCtrl.SearchFriend(Edit1.Text);
  Writeln(Future.Value);
end;

procedure TForm1.Button2Click(Sender: TObject);
var
  Future: IFuture<TKakaoResponse>;
begin
  Future:= KakaoCtrl.AddFriend(Edit1.Text);
end;

procedure TForm1.Button3Click(Sender: TObject);
begin
  KakaoCtrl.SynchronizationFriend;
end;

procedure TForm1.Button4Click(Sender: TObject);
var
  Future: IFuture<TViewFriendInfo>;
begin
  Future:= KakaoCtrl.ViewFriend;
  Writeln(Future.Value.Name);
//  Writeln(Future.Value.ScreenToBase64);
end;

procedure TForm1.Button5Click(Sender: TObject);
var
  FeedsContainer: TFeedsContainer;
begin
  Guard(FeedsContainer, KakaoCtrl.Scan(0).Value);
  if Assigned(FeedsContainer) then
  begin
    Writeln(FeedsContainer.ToJSON);
  end else
  begin
    Writeln('ScanProfile failed');
  end;
end;

procedure TForm1.Button6Click(Sender: TObject);
var
  SearchNewNumberEvent: TSearchNewPhoneNumberEvent;
begin
  Guard(SearchNewNumberEvent, TSearchNewPhoneNumberEvent.Create);
  SearchNewNumberEvent.Email:= 'test@test.com';
  SearchNewNumberEvent.PhoneNumber:= '00011112222';

  Writeln(SearchNewNumberEvent.ToEventJSON);
  Redis.Publish(TOPIC_OTHER_EVENT, SearchNewNumberEvent.ToEventJSON);
end;

procedure TForm1.CheckBox1Click(Sender: TObject);
begin
  Timer1.Enabled:= CheckBox1.Checked;
end;

procedure TForm1.FormClose(Sender: TObject; var Action: TCloseAction);
begin
  Action:= caFree;
  Self.Free;
end;

procedure TForm1.FormCreate(Sender: TObject);
begin
  KakaoCtrl:= TKakaoCtrl.GetInstance;
  Redis:= TRedis.GetInstance;
end;

procedure TForm1.Timer1Timer(Sender: TObject);
var
  ProfilePage: TProfilePage;
begin
  ProfilePage:= GetProfilePage;
  if not ProfilePage.Loaded then
    Exit;

  Writeln(ProfilePage.Current, '/', ProfilePage.Last);
end;

end.
