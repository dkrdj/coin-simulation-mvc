Coin-Simulcation
![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/4757fc46-9d79-4ce0-9d36-8b89652d6839/217d0860-7ecf-4bc6-97e9-e14b87de1b30/Untitled.png)
![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/4757fc46-9d79-4ce0-9d36-8b89652d6839/2a139114-aa05-471a-8a2f-937be6b983f4/Untitled.png)
![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/4757fc46-9d79-4ce0-9d36-8b89652d6839/3b1fc834-5a93-412f-9641-e6bfff1fb475/Untitled.png)
### 프로젝트 소개

Upbit 데이터를 통한 코인 모의투자 서비스

### 제작기간

2023.10 ~ 진행 중

### 스킬

Spring MVC, Spring Data JPA, Spring Security, queryDSL, PostgreSQL, MongoDB, Redis

### 역할

AWS 서버 생성 및 Jenkins 와 Git을 이용한 CI / CD 구축

백엔드 개발

### 프로젝트 결과

- Upbit Websocket API를 통해 코인 정보를 실시간으로 받아 유저에게 제공합니다.
- 유저가 매수, 매도 주문을 통해 모의투자를 할 수 있습니다.
- Upbit로부터 얻은 체결 목록을 통해 유저의 주문을 체결합니다.

### 아키텍처

![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/4757fc46-9d79-4ce0-9d36-8b89652d6839/561f7ae2-d96e-4246-94c8-adb3f744289c/Untitled.png)

### 주요 로직

![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/4757fc46-9d79-4ce0-9d36-8b89652d6839/53eec69d-e0d7-4857-bd27-b103a5a604e6/Untitled.png)

- Upbit한테서 Websocket으로 받은 정보들을 프론트엔드로 그대로 전달
- 체결 내역들은 유저의 주문목록들을 보고 조건에 맞으면 체결시키고, SSE를 통해 유저에게 전달

![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/4757fc46-9d79-4ce0-9d36-8b89652d6839/f30443f7-bb7d-4635-9dc8-1431b5a0c83d/Untitled.png)

### 문제 상황 및 해결 방법

1. upbit에서는 주문 취소 내역을 제공하지 않아서, 실제로 upbit에서 취소가 되었는지를
추정만 할 수 있음. 또한 취소된 구간이 사용자 주문의 앞쪽인지, 뒤쪽인지 구별하지 못함.
→ '모의투자'라는 개념을 역이용하여, 유저에게 모의투자 경험만 줄 수 있으면 된다고 판단,
항상 유저 대기열의 뒤쪽에서 취소가 된다고 가정하여 구현.

2. 짧은 순간에 많은 체결 내역들이 들어와서 DB에 갱신이 제대로 되지 않음.
→비관적 락을 사용하고, @Transaction의 전파 속성을 사용하여 체결-주문해결

3. 퍼포먼스 테스트 결과, 초당 약 2만개의 Insert 및 Update 처리량을 약 2배인 4만5천개까지 개선시킴.
→JDBC bulk Insert를 사용
→ left Join을 inner Join으로 변경
→ JPA 변경감지를 통한 Update를 @DynamicUpdate를 사용하여 개선
