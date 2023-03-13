object Form1: TForm1
  Left = 0
  Top = 0
  BorderIcons = [biSystemMenu, biMinimize]
  ClientHeight = 86
  ClientWidth = 394
  Color = clBtnFace
  Font.Charset = DEFAULT_CHARSET
  Font.Color = clWindowText
  Font.Height = -12
  Font.Name = 'Segoe UI'
  Font.Style = []
  Position = poScreenCenter
  OnCreate = FormCreate
  PixelsPerInch = 96
  TextHeight = 15
  object CheckBox2: TCheckBox
    Left = 24
    Top = 35
    Width = 97
    Height = 17
    Caption = #47588#53356#47196' '#46041#51089
    Checked = True
    State = cbChecked
    TabOrder = 0
    OnClick = CheckBox2Click
  end
  object SS: TServerSocket
    Active = True
    Port = 1234
    ServerType = stNonBlocking
    OnClientConnect = SSClientConnect
    OnClientDisconnect = SSClientDisconnect
    OnClientRead = SSClientRead
    OnClientError = SSClientError
    Left = 292
    Top = 8
  end
end
