# 자율 프로젝트(Icecream)백엔드 로직 정리

- **기본 상태**
  - 모바일 앱에서 RabbitMQ crosswalk 큐로 GPS 전송 (전송 데이터 : user_id, destination_id, 위도, 경도, time_stamp)
  - GPU 서버에서 CCTV 실시간 분석 후 과속차량 발생 시RabbitMQ Hello 큐로 전송 (전송 데이터 : cctv_name, speed)



- **스케쥴러**

  - 매일 자정 **자녀의 목적지 도착 여부**를 기록하는 `Redis` 세팅 : `key = "arrival:destinationId"`, `value = 0` 
  - 매일 자정 전일 **당일의 목표 달성 여부 업데이트(MongoDB) 및 당일 목표 달성 여부(Redis) 초기화**
    - 전일 목표 달성자 업데이트 
      - `MongoDB`의  `어제 날짜 : 0` (미달성)인 유저들을 1(달성)으로 업데이트 
         (무단 횡단 시 -1(실패)로 바꾸는 로직이므로, 0인 유저들이 자정에 1로 바뀜) 
      - `Postgres`의 `goal` 테이블의 연속 목표 달성 현황 기록 +1 
    - 당일 목표 달성 여부 초기화
      - `MongoDB`에 `"당일 날짜 : 0"` 으로 설정
      - `Redis`에 `key = "user_goal:userId` , `value = 0` 으로 초기화
        (무단 횡단 최초 1회 발생 시에만 `MongoDB`의  `goalstatus` 업데이트 및 무단횡단 알림 로직을 수행하기 위해 기록하는 용도)

  - 매일 오전 8시 **목표에 도달한 자녀, 부모들에게 알림 전송**

  

- **RabbitMQ MessageQueue Listener**

  - `GPSMessageListener(crosswalk Queue 구독)`

    - GPS가 수신되는 경우 아래 3가지 로직 한 번에 실행

      - 유저가 횡단보도 영역에 위치하는지 판별 (`crosswalkService`)

      - 목적지 도착 알림 (`destinationService`)

      - 무단 횡단 판별 및 알림(`jaywalkingCheckService`) + 목표 달성 실패 기록

  - `CCTVMessageListener(hello Queue 구독)` 

    - 과속 차량을 탐지한 CCTV 정보를 수신하는 경우 과속 위험 알림 로직 실행

  

- **백엔드 로직**

  1. 유저 횡단보도 영역에 위치하는지 판별 (`crosswalkService`)

     1. `GPSMessageListener(crosswalk Queue)`의 위도,경도 데이터를 바탕으로 위치 좌표 형성
     2. Postgres의 crosswalk(횡단보도) 테이블의 모든 레코드를 순회하며 위치 좌표가 특정 횡단보도 영역에 위치하는지 검사
     3. `GPSMessageListener(crosswalk Queue)`의 `user_id` 데이터를 바탕으로 아래 로직 수행
        - 위치 좌표가 횡단보도 영역에 위치하는 경우 -> `Redis`의 횡단보도 이름을 조회하여 `user_id`가 있는지 검사 -> `user_id`가 없으면 Redis 횡단보도 이름에 user_id 추가
        - 위치 좌표가 횡단보도 영역에 위치하지 않는 경우 -> `Redis`의 횡단보도 이름을 조회하여 `user_id`가 있는지 검사 -> `user_id`가 있으면 `Redis` 횡단보도 이름에서 `user_id` 제거
     4. 1,2번 로직을 통해 Redis에 `Key = 횡단보도 이름`, `Value = user_id` 로 특정 횡단보도 영역에 속해있는 과속 알림 대상 유저들 실시간 갱신

     

  2. 과속 위험 알림 (`notificationService`, `cctvMessageListenService`, `redisListenService`)

     1. `CCTVMessageListener(hello Queue)`의 cctv_name 데이터를 바탕으로 매핑된 횡단보도 이름 추출
     2. 레디스에서 횡단보도 이름을 조회하여 횡단보도 영역에 포함된 user_id 리스트로 추출
     3. `CCTVMessageListener(hello Queue)`의 speed에 맞게 유저에게 과속 위험 알림 전송

     

  3. 목적지 도착 알림 (`destinationService`)

     1. `GPSMessageListener(crosswalk Queue)`의 `user_id`, `destination_id`, 위도, 경도 데이터를 사용

     2. `destination_id`를 통해 Postgres의 `destination` 테이블에서 목적지 영역에 속하는지 검사

     3. 자녀의 목적지 도착 여부를 기록하는 `Redis`를 조회

        1. `key = "arrival:destinationId"` 의 `value`가  0인 유저에 대해 1(목적지 도착)로 변경
        2. `user_id`와 매핑 된 부모에게 알림 전송

        

  4. 무단 횡단 판별(`jaywalkingCheckService`)

     1. `GPSMessageListener(crosswalk Queue)`의 위도,경도 데이터를 바탕으로 위치 좌표 형성
     2. 당일 무단 횡단을 기록하는 `Redis key = "user_goal:userId`를 조회
     3. `Redis`의 `value`가 0인 경우에만 (당일 무단횡단 기록 없는 경우)
        1. `Redis`의 `value`를 -1로 변경(무단 횡단 최초 1회 발생)
        2. 자녀 및 연결된 부모에게 무단 횡단 알림 전송
        3. `Postgres goal` 테이블의 연속 목표 달성 현황을 0으로 수정

