unit BitmapUtils;

interface

uses
  Winapi.Windows, Winapi.Messages, System.Classes, System.SysUtils, System.NetEncoding, Vcl.Graphics;

function GetProfileScreen(hWindow: THandle) : TBitmap;
function BitmapToBase64String(Bitmap: TBitmap): string;

implementation

function GetProfileScreen(hWindow: THandle): TBitmap;
var
  DC: HDC;
  WinRect: TRect;
  Width: Integer;
  Height: Integer;
begin
  Result:= TBitmap.Create;

  GetClientRect(hWindow, WinRect);
  DC:= GetDC(hWindow);
  try
    Width:= WinRect.Right - WinRect.Left;
    Height:= WinRect.Bottom - WinRect.Top;

    Result.Height:= Height - 40 - Abs(600 - Height);
    Result.Width:= Width;
    BitBlt(Result.Canvas.Handle, 0, 0, Width, Height, DC, 0, 40, SRCCOPY);
  finally
    ReleaseDC(hWindow, DC);
  end;
end;

function BitmapToBase64String(Bitmap: TBitmap): string;
var
  MemStream: TMemoryStream;
  Bytes: TBytes;
begin
  MemStream:= TMemoryStream.Create;
  try
    Bitmap.SaveToStream(MemStream);
    SetLength(Bytes, MemStream.Size);
    MemStream.Position:= 0;
    MemStream.ReadBuffer(Bytes[0], MemStream.Size);
    Result:= TNetEncoding.Base64.EncodeBytesToString(Bytes);
  finally
    MemStream.Free;
  end;
end;


end.
