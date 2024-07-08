## 카카오스캔 - 카톡 프로필 조회 서비스
카카오스캔은 번호를 저장없이 상대방의 카카오톡 기본 프로필을 확인할 수 있는 사이트에요.   
일상 생활을 하다보면 모르는 번호로 전화가 왔을 때, 번호 저장 없이 신원을 확인하고 싶을 때가 있을거에요.
또 누군가는 자신의 카카오톡 지인들에 대해 멀티 프로필 여부를 확인해보고 싶기도 할거에요. 카카오스캔은 이러한 사람들의 일상에 소소한 도움이 될 것이라 믿어 개발하게 되었습니다.

## 기술 스택
- 서버
    - Java
    - Spring Boot
    - Spring Data JPA
    - Spring Security
    - Redis
    - WebSocket
    - MySQL
    
- 클라이언트
  - TypeScript
  - React
  - Redux Toolkit
  - Tailwind CSS
  - SockJS/StompJS

- 윈도우
  - Delphi
  - Win32 API
  - Dynamic Link Library
  - Hooking
  - Redis
 
 ## 데모 시작하기
서버 `docker-compose.yml`에 알맞은 환경 변수를 설정해주세요.
```
version: '3.8'
services:
  redis:
    image: redis:latest
    ports:
      - 6379:6379
    command: redis-server --requirepass ${REDIS_PASSWORD}

  kakaoscan-server:
    image: ekfkawl/kakaoscan-server:v240416180547
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=dev
      - SPRING_DATA_REDIS_HOST=redis
      - REDIS_PASSWORD=${REDIS_PASSWORD}
      - TZ=${TZ}
      - LANG=${LANG}
      - DATASOURCE_URL=${DATASOURCE_URL}
      - DATASOURCE_USERNAME=${DATASOURCE_USERNAME}
      - DATASOURCE_PASSWORD=${DATASOURCE_PASSWORD}
      - JWT_SECRET_KEY=${JWT_SECRET_KEY}
      - SMTP_USERNAME=${SMTP_USERNAME}
      - SMTP_PASSWORD=${SMTP_PASSWORD}
      - CURRENT_BASE_URL=http://localhost:8080
      - B_ACCOUNT=DEMO
    depends_on:
      - redis
```

클라이언트 `docker-compose.yml`에 알맞은 환경 변수를 설정해주세요.
```
version: '3.8'
services:
  kakaoscan-client:
    image: ekfkawl/kakaoscan-client:v240416180902
    ports:
      - "3000:3000"
    environment:
      - WDS_SOCKET_PORT=0
      - REACT_APP_GOOGLE_OAUTH_CLIENT_ID=${REACT_APP_GOOGLE_OAUTH_CLIENT_ID}
      // 채널톡
      - REACT_APP_CHANNEL_PLUGIN_KEY=${REACT_APP_CHANNEL_PLUGIN_KEY}
      - REACT_APP_CHANNEL_USER_SECRET_KEY=${REACT_APP_CHANNEL_USER_SECRET_KEY}
```

서버 시작
```
cd .. // server and cliet
docker-compose up -d
```

## 제작자 블로그
https://blog.naver.com/ekfkawl0/222969011120
