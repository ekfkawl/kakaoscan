unit ClientInstance;

interface

uses
  Winapi.Windows, Winapi.Messages, System.SysUtils, System.Variants, System.Classes, Generics.Collections, System.Generics.Defaults;

type
  TClientMessage = record
    ConnectTime: LONG64;
    QueueTimeOut: LONG64;
    Heartbeat: LONG64;
    SocketHandle: NativeInt;
    RemoteAddr: String;
    RemoteAddr2: String;
    Email: String;
    Msg: String;
    WebResponMsg: String;
    IsSendReady: Boolean;
    Session: String;

    class function Create(ConnectTime: LONG64; Heartbeat: LONG64; SocketHandle: NativeInt; RemoteAddr: String; Msg: String): TClientMessage; static;
  end;

  TClient = class
    private
      FDictMessage: TDictionary<NativeInt, TClientMessage>;
    public
      property DictMessage:TDictionary<NativeInt, TClientMessage> read FDictMessage write FDictMessage;

      function ToQueueList: TList<TClientMessage>;

      constructor Create;
      destructor Destroy; override;
  end;

implementation

uses
  uKakaoServer;

var
  IntegerComparer: IComparer<Integer>;

{ TClientMessage }

class function TClientMessage.Create(ConnectTime: LONG64; Heartbeat: LONG64; SocketHandle: NativeInt; RemoteAddr: String; Msg: String): TClientMessage;
var
  ClientMessage: TClientMessage;
begin
  ClientMessage.ConnectTime:= ConnectTime;
  ClientMessage.QueueTimeOut:= 0;
  ClientMessage.Heartbeat:= Heartbeat;
  ClientMessage.SocketHandle:= SocketHandle;
  ClientMessage.RemoteAddr:= RemoteAddr;
  ClientMessage.Msg:= Msg;
  ClientMessage.WebResponMsg:= '';
  ClientMessage.IsSendReady:= False;
  ClientMessage.Session:= '';

  Result:= ClientMessage;
end;


{ TClient }

// 먼저 접속한 순서로 정렬
function TClient.ToQueueList: TList<TClientMessage>;
var
  ClientList: TList<TClientMessage>;
begin
  ClientList:= TList<TClientMessage>.Create;
  ClientList.Clear;
  try
    try
      for var o in FDictMessage do
      begin
        ClientList.Add(o.Value);
      end;

      if ClientList.Count > 1 then
      begin
        ClientList.Sort(
          TComparer<TClientMessage>.Construct(
            function(const Left, Right: TClientMessage): Integer
            begin
              Result:= IntegerComparer.Compare(Right.ConnectTime, Left.ConnectTime);
            end
          ));
      end;
    except;
    end;
  finally
    Result:= ClientList;
  end;
end;

constructor TClient.Create;
begin
  FDictMessage:= TDictionary<NativeInt, TClientMessage>.Create;
end;

destructor TClient.Destroy;
begin
  FDictMessage.Free;

  inherited;
end;

initialization
  IntegerComparer:= TComparer<Integer>.Default;

end.
