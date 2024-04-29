### Coin-Simulcation
<img width="1298" alt="Untitled" src="https://github.com/dkrdj/coin-simulation-mvc/assets/109264979/594a57d6-ffb0-4bb5-a02e-1006ece3e17d">
<img width="1271" alt="Untitled 1" src="https://github.com/dkrdj/coin-simulation-mvc/assets/109264979/57e13db7-de08-4478-9d1d-6c51aa3b9bbe">
<img width="1107" alt="Untitled 2" src="https://github.com/dkrdj/coin-simulation-mvc/assets/109264979/dcb49a1d-d216-4948-9b75-f1a810cc9653">

### 프로젝트 소개

Upbit 데이터를 통한 코인 모의투자 서비스

### 제작기간

2023.10 ~ 2024.04

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

![Untitled 3](https://github.com/dkrdj/coin-simulation-mvc/assets/109264979/7cf69de2-efe2-4e5f-8266-190e47db1b0f)

### 주요 로직

![Untitled 4](https://github.com/dkrdj/coin-simulation-mvc/assets/109264979/e2c2fe49-7f99-4279-b41c-eb15a7d7853a)

- Upbit한테서 Websocket으로 받은 정보들을 프론트엔드로 그대로 전달
- 체결 내역들은 유저의 주문목록들을 보고 조건에 맞으면 체결시키고, SSE를 통해 유저에게 전달

![Untitled 5](https://github.com/dkrdj/coin-simulation-mvc/assets/109264979/0c16938a-d449-4382-87c7-6689e4bc650b)

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
