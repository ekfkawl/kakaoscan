unit uKakaoSDKExport;

interface

uses
  Winapi.Windows, System.SysUtils, System.Classes, System.StrUtils, System.Types, KakaoUtils, uKakaoHook;

implementation

function AddFriend(PhoneNumber: String): Boolean;
begin
  Result:= Kakao.AddFriend(PhoneNumber);
end;

function GetSharableMemory: Pointer;
begin
  Result:= Kakao.GetSharableMemory;
end;

function SearchFriend(CustomName: String): Boolean;
begin
  Result:= Kakao.SearchFriend(CustomName);
end;

function ViewFriend: Boolean;
begin
  Result:= Kakao.ViewFriend;
end;

function ViewProfileImage(CustomName: String): Boolean;
begin
  Result:= Kakao.ViewProfileImage(CustomName);
end;

function BlockAndClearFriend: Boolean;
begin
  Result:= Kakao.BlockAndClearFriend;
end;

function ViewPreviewImage: Boolean;
begin
  Result:= Kakao.ViewPreviewImage;
end;

exports
  AddFriend,
  GetSharableMemory,
  SearchFriend,
  ViewFriend,
  ViewProfileImage,
  BlockAndClearFriend,
  ViewPreviewImage;

end.
