unit KakaoAPI;

interface

uses
  Winapi.Windows, System.Classes;

const
  KakaoSDK32 = 'KakaoSDK32.dll';

function AddFriend(PhoneNumber: String): Boolean; external KakaoSDK32 name 'AddFriend';
function SearchFriend(CustomName: String): Boolean; external KakaoSDK32 name 'SearchFriend';
function GetSharableMemory: Pointer; external KakaoSDK32 name 'GetSharableMemory';
function ViewFriend: Boolean; external KakaoSDK32 name 'ViewFriend';
function ViewProfileImage(CustomName: String): Boolean; external KakaoSDK32 name 'ViewProfileImage';
function BlockAndClearFriend: Boolean; external KakaoSDK32 name 'BlockAndClearFriend';

implementation

uses
  uKakaoServer, SharableMemory;

end.
