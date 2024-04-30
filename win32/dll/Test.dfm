object Form1: TForm1
  Left = 0
  Top = 0
  Caption = 'Test'
  ClientHeight = 252
  ClientWidth = 588
  Color = clBtnFace
  Font.Charset = DEFAULT_CHARSET
  Font.Color = clWindowText
  Font.Height = -12
  Font.Name = 'Segoe UI'
  Font.Style = []
  OnClose = FormClose
  OnCreate = FormCreate
  PixelsPerInch = 96
  TextHeight = 15
  object Button1: TButton
    Left = 8
    Top = 40
    Width = 129
    Height = 33
    Caption = 'SearchFriend'
    TabOrder = 0
    OnClick = Button1Click
  end
  object Edit1: TEdit
    Left = 8
    Top = 11
    Width = 129
    Height = 23
    TabOrder = 1
  end
  object Button2: TButton
    Left = 8
    Top = 79
    Width = 129
    Height = 34
    Caption = 'AddFriend'
    TabOrder = 2
    OnClick = Button2Click
  end
  object Button3: TButton
    Left = 200
    Top = 40
    Width = 129
    Height = 33
    Caption = 'SyncFriend'
    TabOrder = 3
    OnClick = Button3Click
  end
  object Button4: TButton
    Left = 200
    Top = 79
    Width = 129
    Height = 34
    Caption = 'ViewFriend'
    TabOrder = 4
    OnClick = Button4Click
  end
  object CheckBox1: TCheckBox
    Left = 8
    Top = 168
    Width = 97
    Height = 17
    Caption = 'ProfilePage'
    TabOrder = 5
    OnClick = CheckBox1Click
  end
  object Button5: TButton
    Left = 200
    Top = 119
    Width = 129
    Height = 34
    Caption = 'ScanProfile'
    TabOrder = 6
    OnClick = Button5Click
  end
  object Button6: TButton
    Left = 379
    Top = 39
    Width = 201
    Height = 35
    Caption = 'Pub SearchNewNumberEvent'
    TabOrder = 7
    OnClick = Button6Click
  end
  object Button7: TButton
    Left = 8
    Top = 119
    Width = 129
    Height = 34
    Caption = 'OpenId'
    TabOrder = 8
    OnClick = Button7Click
  end
  object Timer1: TTimer
    Enabled = False
    OnTimer = Timer1Timer
    Left = 120
    Top = 136
  end
end
