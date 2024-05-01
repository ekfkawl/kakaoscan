unit Test;

interface

uses
  Winapi.Windows, Winapi.Messages, System.SysUtils, System.Variants, System.Classes, Vcl.Graphics,
  Vcl.Controls, Vcl.Forms, Vcl.Dialogs, Vcl.StdCtrls, KakaoCtrl, System.Threading, KakaoHook,
  KakaoResponse, Vcl.ExtCtrls, KakaoProfilePageUtil, KakaoProfile, GuardObjectUtil, RedisUtil, RedisConfig,
  SearchNewPhoneNumberEvent, KakaoId;

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
    Button7: TButton;
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
    procedure Button7Click(Sender: TObject);
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
  Future:= KakaoCtrl.AddFriend(Edit1.Text, rtNumber);
end;

procedure TForm1.Button3Click(Sender: TObject);
begin
  KakaoCtrl.SynchronizationFriend;
end;

var
  ViewFriendFuture: IFuture<TViewFriendInfo>;
procedure TForm1.Button4Click(Sender: TObject);
begin
  ViewFriendFuture:= KakaoCtrl.ViewFriend(0);
  Writeln(ViewFriendFuture.Value.Handle);
  Writeln(ViewFriendFuture.Value.Name);
//  Writeln(Future.Value.ScreenToBase64);
end;

procedure TForm1.Button5Click(Sender: TObject);
var
  FeedsContainer: TFeedsContainer;
begin
  SetProfileImageViewerType(rtViewTypeProfile);
  Guard(FeedsContainer, KakaoCtrl.Scan(ViewFriendFuture.Value.Handle).Value);
  if Assigned(FeedsContainer) then
  begin
    Writeln(FeedsContainer.ToJSON);
  end else
  begin
    Writeln('ScanProfile failed');
  end;
  Writeln('--------------------------');
end;

procedure TForm1.Button6Click(Sender: TObject);
var
  SearchNewNumberEvent: TSearchNewPhoneNumberEvent;
begin
  Guard(SearchNewNumberEvent, TSearchNewPhoneNumberEvent.Create);
  SearchNewNumberEvent.Email:= 'test@test.com';
  SearchNewNumberEvent.PhoneNumber:= '00011112222';

  Writeln(SearchNewNumberEvent.ToEventJSON);
  Redis.Publish(OTHER_EVENT_TOPIC, SearchNewNumberEvent.ToEventJSON);
end;

procedure TForm1.Button7Click(Sender: TObject);
var
  Future: IFuture<TOpenIdResult>;
begin
  Future:= KakaoCtrl.OpenId;
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
