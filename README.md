![og_image1](https://user-images.githubusercontent.com/99597985/209767752-fbb1da3a-c0cb-41c8-89c4-9da6fa8f4a95.png)

카카오스캔[(https://kakaoscan.com)](https://kakaoscan.com)은 무엇입니까?
--------------------------------------
카카오스캔은 클라이언트가 요청한 전화번호로 카카오톡 유저 프로필 정보를 가져옵니다.<br>
다음과 같은 일상 상황에 사용하기 적합합니다.

* 상대방의 멀티 프로필이 아닌 기본 프로필을 확인하고 싶음
* 상대방이 나를 차단했는지 확인하고 싶음
* 번호 저장 없이 프로필을 확인하고 싶음<br/><br/>
<img src="https://github.com/ekfkawl/kakaoscan/blob/main/preview.gif?raw=true" width="375"/>

카카오스캔은 어떻게 작동합니까?
--------------------------------------
현재 카카오톡에서는 유저 프로필을 추출하는 공식적인 API를 제공하지 않습니다.<br>
유저 정보를 얻기위해 윈도우에서 시뮬레이션하여 캡쳐합니다. 한 서버로 동시에 2개 이상 요청은 처리할 수 없기에 대기열을 구성합니다.<br>
서로 다른 레이어간 실시간 통신을 위해 TCP소켓을 사용합니다. 윈도우 서버에서 가공이 완료되었으면 Netty TCP Client로 메세지를 전달하고 이후 WebSocket Server 핸들러에서 View로 전달합니다. 기본적인 구조는 [여기서](https://user-images.githubusercontent.com/99597985/204060706-0c8c0c84-0ea2-4b18-af25-5e865feac6d9.png) 확인할 수 있습니다.<br/><br/>


사용 언어/스택
--------------------------------------
* __Pascal__  
  * TCP Socket Server
  * 카카오톡 시뮬레이션
  
* __Java/Spring Boot__  
  * API Server
  * Netty TCP Socket Client
  * WebSocket Server
  * JPA
  
* __Script__
  * WebSocket Client
