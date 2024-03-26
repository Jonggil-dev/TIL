# Redis 도커 컨테이너 실행 방법

- **Redis 의 경우 별도로 환경 세팅을 커스텀 할 게 없으므로 공식 이미지 사용**

- 원래는 클라우드 수준의 AWS 보안 그룹의 인바운드 규칙 포트와, 인스턴스(서버) 수준의 방화벽(ufw) 포트를 모두 열어야 됨

- 싸피에서 제공받은 EC2 서버의 AWS 보안 그룹 설정은 모든 포트에 열려 있는것으로 추정
  (cli를 통해 확인 가능한데 확인하기 위해 AWS CLI를 다운받아야 되는데 메모리 잡아먹는다고 해서 안깔음)

- 그래서 그냥 서버 내 ufw 포트만 열어두면 됨

- **실행 과정**

  - **컨테이너 실행**

    - ````bash
      docker run -d --name redis \
      -p 6379:6379 \
      -v redis-data:/data \
      redis:latest \
      redis-server --requirepass "yourpassword" --appendonly yes
      ````
      
      - `--name some-redis`: 이 옵션은 실행되는 컨테이너에 `some-redis`라는 이름을 지정합니다. 컨테이너 이름은 Docker 호스트에서 컨테이너를 식별하는 데 사용됩니다.
      - `-d`: 이 옵션은 컨테이너를 백그라운드에서 실행하라는 명령입니다. 이를 통해 터미널이 컨테이너 실행에 바인딩되지 않고, 사용자는 새 명령을 입력할 수 있는 프롬프트로 돌아갑니다.
      - `-p 6379:6379`: 포트 매핑 옵션입니다. 호스트의 6379 포트를 컨테이너의 6379 포트에 연결합니다. Redis는 기본적으로 6379 포트를 사용하므로, 이 설정을 통해 호스트 시스템(또는 네트워크 내의 다른 시스템)에서 컨테이너의 Redis 서버에 접근할 수 있습니다.
      - `-v redis-data:/data`: 이 옵션은 Docker Volume을 사용하여 데이터 지속성을 관리합니다. `redis-data`라는 이름의 Docker Volume을 컨테이너 내의 `/data` 디렉토리에 매핑합니다. Redis 데이터는 이 디렉토리에 저장되며, 컨테이너가 삭제되거나 재시작되어도 데이터는 유지됩니다.
      - `redis:latest`: 이 부분은 사용할 이미지 이름을 지정합니다. 여기서는 Docker Hub의 공식 `redis` 이미지를 사용합니다.
      - `redis-server` : 뒤에 오는 명령어 들은 실행되는 `redis-server`에 전달되는 명령어임
      - ` --requirepass "yourpassword""` : **redis 서버의 비밀번호 설정**
      - `--appendonly yes`: 컨테이너가 시작될 때 실행할 명령어입니다. `--appendonly yes` 옵션은 Append-Only File (AOF) 지속성 모드를 활성화합니다. AOF 모드에서는 모든 쓰기 작업(데이터 변경)이 로그 파일에 기록되므로, 서버가 다운되었다가 재시작되어도 데이터를 복구할 수 있습니다.
  
  - **REDIS CLI 접근**
  
    - CLI 접근과 동시에 비밀번호 인증하는 방법
  
      ```bash
      docker exec -it redis redis-cli -a yourpassword
      ```
  
    - CLI 접근 후에 비밀번호 인증하는 방법
  
      ```bash
      docker exec -it redis redis-cli
      AUTH yourpassword
      ```
  
  - **Redis 사용 명령어**
  
    - `SELECT number`: number에 해당하는 데이터베이스 접근
    - `KEYS *` :  Redis 데이터베이스에 저장된 모든 키를 검색하고 반환
  
    - `SET key value`: 주어진 키에 문자열 값을 저장합니다.
      - 예: `SET mykey somevalue`
  
    - `GET key`: 주어진 키의 값을 검색합니다.
      - 예: `GET mykey`
  
    - `DEL key [key ...]` : 하나 이상의 키와 그에 해당하는 값을 삭제합니다.
      - 예: `DEL mykey`
    - `FLUSHDB` : 현재 데이터베이스의 모든 키를 삭제합니다.
    - `FLUSHALL` : 모든 데이터베이스의 모든 키를 삭제합니다.