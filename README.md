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

![123.png](https://user-images.githubusercontent.com/99597985/230585788-51d263d9-a471-4678-9238-fa9277163556.png)

**프로필 조회 상세 과정**

1. 사용자가 프로필을 조회할 전화번호를 요청합니다.
2. 웹소켓 서버에 접속합니다.
    1. 전화번호 요청 메세지를 받으면, 요청 시간을 큐에 저장합니다.
    2. 사용자는 프로필 조회 결과를 수신받기 전 까지, 서버와 연결 된 상태여야 합니다.
    따라서 사용자는 지속적으로 하트비트 메세지를 보내고, 서버는 하트비트 메세지를 보낸 사용자(세션)에 대해 getTurn() 메서드를 호출하면서 순서대로 요청을 처리할 수 있도록, 대기 큐를 형성하고 대기 상황을 사용자에게 보냅니다.
3. 사용자의 Turn이 오면 Netty를 통해, VPS 클라우드에서 실행 중인 Window TCP Server에 접속합니다.
그리고 전화번호를 비롯한 유저 정보들을 TCP Server에 보냅니다.
4. TCP Server에서 수신 받은 메세지를 기반으로 카카오톡 데스크톱 앱을 제어하면서 프로필 추출 작업을 진행하고, 조회 결과를 JSON형식으로 가공하여 Netty TCP Client에 보냅니다.
5. 이 때, 프로필 조회 결과 메세지를 OSI Layer4에서 수신받았으므로, 계층이 다른 Layer7인 웹소켓 프로토콜에서는 당장 사용자에게 메세지를 보낼 수 없습니다.
따라서 Layer4, Netty TCP Client에서 수신 받은 메세지를 ‘Bridge Instance’라는 static 전역 객체에 임시 저장합니다.
6. 사용자는 아직 프로필 조회 결과가 브라우저에 렌더링 된 상태가 아니기 때문에, 지속적으로 하트비트 메세지를 웹소켓 서버에 보내고 있는 상황입니다.
따라서 웹소켓 서버에서 하트비트 메세지를 수신 받으면서, ‘Bridge Instance’ 객체에 값이 존재하는지 지속적으로 확인 할 수 있습니다. 
만약 값이 있다면 프로필 조회를 완료한 것이니, 값을 읽어서 사용자(세션)에게 전달하고, ‘Bridge Instance’에 저장 된 값을 clear 합니다.
7. 이제 사용자는 수신 받은 메세지를 기반으로, 적절한 자바스크립트를 통해 브라우저에 프로필 조회 결과를 렌더링 합니다.   
   
   
   
사용 언어/스택
--------------------------------------
- **Backend**
    - Java
    - Spring Boot
    - Spring Data JPA
    - Spring Security
    - Spring Batch
    - Kafka
    - Netty TCP Socket Client
    - WebSocket Server
    - DB
        - Redis
        - MySQL
    - Cloud Service
        - AWS EC2
        - AWS S3
        - AWS Elastic Beanstalk
        - Vultr VPS
        
- **Frontend**
    - WebSocket Connect
    
- **Window Desktop**
    - Delphi
    - TCP Socket Server
    - Dynamic Link Library
        - Win32 API, Hooking 카카오톡 프로필 추출
  
