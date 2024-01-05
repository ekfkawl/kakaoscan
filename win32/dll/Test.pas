unit Test;

interface

uses
  Winapi.Windows, Winapi.Messages, System.SysUtils, System.Variants, System.Classes, Vcl.Graphics,
  Vcl.Controls, Vcl.Forms, Vcl.Dialogs, Vcl.StdCtrls, KakaoCtrl, System.Threading,
  KakaoResponse;

type
  TForm1 = class(TForm)
    Button1: TButton;
    Edit1: TEdit;
    Button2: TButton;
    Button3: TButton;
    Button4: TButton;
    procedure FormCreate(Sender: TObject);
    procedure Button1Click(Sender: TObject);
    procedure Button2Click(Sender: TObject);
    procedure Button3Click(Sender: TObject);
    procedure Button4Click(Sender: TObject);
  private
    { Private declarations }
  public
    { Public declarations }
  end;

var
  Form1: TForm1;
  KakaoCtrl: TKakaoCtrl;

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
  Future: IFuture<string>;
begin
  Future:= KakaoCtrl.ViewFriend;
  Writeln(Future.Value);
end;

procedure TForm1.FormCreate(Sender: TObject);
begin
  KakaoCtrl:= TKakaoCtrl.GetInstance;
end;

end.
