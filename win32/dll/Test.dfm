object Form1: TForm1
  Left = 0
  Top = 0
  Caption = 'Test'
  ClientHeight = 441
  ClientWidth = 624
  Color = clBtnFace
  Font.Charset = DEFAULT_CHARSET
  Font.Color = clWindowText
  Font.Height = -12
  Font.Name = 'Segoe UI'
  Font.Style = []
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
end
