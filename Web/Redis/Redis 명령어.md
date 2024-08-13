# Redis 명령어

### 데이터 입력
1. **키에 값 설정**
   - `SET key value`
     - 예: `SET username alice`

2. **리스트에 값 추가**
   - `LPUSH key value` (리스트의 왼쪽에 값 추가)
     - 예: `LPUSH mylist apple`
   - `RPUSH key value` (리스트의 오른쪽에 값 추가)
     - 예: `RPUSH mylist orange`

3. **해시에 값 설정**
   - `HSET key field value`
     - 예: `HSET user1 name Alice`

4. **세트에 값 추가**
   - `ADD key member`
     - 예: `SADD myset apple`

### 데이터 조회
1. **키의 값 검색**
   - `GET key`
     - 예: `GET username`

2. **리스트의 범위로 값 검색**
   - `LRANGE key start stop`
     - 예: `LRANGE mylist 0 -1`

3. **해시에서 값 검색**
   - `HGET key field`
     - 예: `HGET user1 name`
   - `HGETALL key`
     - 예: `HGETALL user1`

4. **세트의 모든 멤버 조회**
   - `SMEMBERS key`
     - 예: `SMEMBERS myset`

### 데이터 수정
1. **키의 값 업데이트**
   - `SET key value` (이미 존재하는 키의 값을 새로운 값으로 업데이트)
     - 예: `SET username bob`

2. **해시의 필드 값 업데이트**
   - `HSET key field value`
     - 예: `HSET user1 name Bob` (기존 필드 업데이트)

### 데이터 삭제
1. **키 삭제**
   - `DEL key`
     - 예: `DEL username`

2. **리스트에서 특정 값 삭제**
   - `LREM key count value`
     - 예: `LREM mylist 1 apple` (리스트에서 'apple'을 한 번만 삭제)

3. **해시 필드 삭제**
   - `HDEL key field`
     - 예: `HDEL user1 name`

4. **세트에서 멤버 삭제**
   - `SREM key member`
     - 예: `SREM myset apple`

5. **데이터베이스 비우기**
   - `FLUSHDB` (현재 데이터베이스)
   - `FLUSHALL` (모든 데이터베이스)
