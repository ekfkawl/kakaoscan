object Form1: TForm1
  Left = 0
  Top = 0
  BorderIcons = [biSystemMenu, biMinimize]
  ClientHeight = 519
  ClientWidth = 653
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
  object Label1: TLabel
    Left = 8
    Top = 8
    Width = 109
    Height = 15
    Caption = 'ActiveConnections : '
  end
  object ListBox1: TListBox
    Left = 8
    Top = 29
    Width = 637
    Height = 433
    ItemHeight = 15
    TabOrder = 0
  end
  object CheckBox1: TCheckBox
    Left = 8
    Top = 468
    Width = 97
    Height = 17
    Caption = #49828#53356#47204
    Checked = True
    State = cbChecked
    TabOrder = 1
  end
  object CheckBox2: TCheckBox
    Left = 8
    Top = 491
    Width = 97
    Height = 17
    Caption = #47588#53356#47196' '#46041#51089
    Checked = True
    State = cbChecked
    TabOrder = 2
    OnClick = CheckBox2Click
  end
  object BitBtn1: TBitBtn
    Left = 536
    Top = 468
    Width = 109
    Height = 40
    Caption = #47196#44536' '#51221#47532
    TabOrder = 3
    OnClick = BitBtn1Click
  end
  object SS: TServerSocket
    Active = True
    Port = 1234
    ServerType = stNonBlocking
    OnClientConnect = SSClientConnect
    OnClientDisconnect = SSClientDisconnect
    OnClientRead = SSClientRead
    OnClientError = SSClientError
    Left = 340
    Top = 400
  end
end
