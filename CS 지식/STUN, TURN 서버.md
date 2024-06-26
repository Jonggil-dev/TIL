# STUN, TURN 서버

- 참고 자료 : https://terianp.tistory.com/178

### 1. STUN 서버

- 쉽게 말하면, 자신의 공용 IP 주소를 찾아주는 서버 
- webRTC 기술 사용 시 필요함. peer to peer 연결을 위해 각 클라이언트의 공용 IP주소가 필요하기 때문
- STURN 서버는 구글에서 제공해 주기 때문에 그곳으로 요청만 보내면 됨
- 로컬 환경(로컬 환경의 장치) 내에서는 NAT(Network Address Translation)에 의해 공용 IP 주소(인터넷에서 직접적으로 사용되는 접근 가능한 주소)가 관리되기 때문에, 개별 장치들은 자신의 공용 IP 주소를 직접 알 수가 없음 (사설 IP 밖에 모름)
  - cmd에 ipconfig 명령어를 통해 나타는 주소(사설 IP)와 naver에 "내 IP 주소"를 검색 했을 때 나오는 주소(공용 IP)가 다름. 즉 본인의 공용 IP주소를 알려면 naver 처럼 외부 서버의 도움을 받아야 함

### 2. TURN 

- STUN 서버로 해결할 수 없는 경우 두 장치(브라우저) 간 중계자 역할을 하는 서버
- 통신하는 두 장치 간의 데이터 전송은 TURN 서버를 경유하여 이루어짐
- 즉, 복잡한 NAT 또는 엄격한 방화벽 정책을 가진 네트워크 환경에서 peer to peer 연결이 불가능 할 때 TURN 서버가 중계 역할을 하여 두 장치간의 데이터 전송을 도와주는 서버임.