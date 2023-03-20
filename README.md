![og_image1](https://user-images.githubusercontent.com/99597985/209767752-fbb1da3a-c0cb-41c8-89c4-9da6fa8f4a95.png)

카카오스캔[(https://kakaoscan.com)](https://kakaoscan.com)은 무엇입니까?
--------------------------------------
카카오스캔은 클라이언트가 요청한 전화번호로 카카오톡 유저 프로필 정보를 가져옵니다.<br>
다음과 같은 일상 상황에 사용하기 적합합니다.

* 상대방의 멀티 프로필 여부를 확인하고, 기본 프로필을 조회하고 싶을 때
* 모르는 번호로 전화가 왔을 때, 번호 저장 없이 신원을 확인하고 싶을 때<br/><br/>
<img src="https://github.com/ekfkawl/kakaoscan/blob/main/preview.gif?raw=true" width="375"/>

카카오스캔은 어떻게 작동합니까?
--------------------------------------
현재 카카오톡에서는 유저 프로필을 추출하는 공식적인 API를 제공하지 않습니다.<br>
프로필을 얻기 위해 윈도우 서버에서 pc카카오톡을 직접 조작하여 프로필 정보를 추출합니다.<br>
한 서버로 동시에 2개 이상 조회 요청은 처리할 수 없기에, 작업 중에 요청이 온다면 소켓 대기열을 구성합니다.<br>
서버에서 프로필 조회 작업을 마치면 s3에 파일을 업로드합니다. 이후 layer4에서 수신한 프로필 데이터를 layer7에 전송하여 브라우저에 출력합니다.
기본적인 구조는 [여기서](https://user-images.githubusercontent.com/99597985/204060706-0c8c0c84-0ea2-4b18-af25-5e865feac6d9.png) 확인할 수 있습니다.<br/><br/>


사용 언어/스택
--------------------------------------
* Window Desktop
  * TCP Socket Server
  * Kakaotalk Extract Profile DLL Library
  
* Backend
  * Spring Boot
  * Spring Data JPA
  * Spring Security
  * Netty TCP Socket Client
  * WebSocket Server
  * Kafka
  * Redis
  * AWS (EC2, S3, Elastic Beanstalk)
  * MySQL
  
* Frontend
  * WebSocket Connect
  
