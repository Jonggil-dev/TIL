# MariaDB 도커 컨테이너 실행 방법

- **MariaDB의 경우 별도로 환경 세팅을 커스텀 할 게 없으므로 공식 이미지 사용**

- 원래는 클라우드 수준의 AWS 보안 그룹의 인바운드 규칙 포트와, 인스턴스(서버) 수준의 방화벽(ufw) 포트를 모두 열어야 됨

- 싸피에서 제공받은 EC2 서버의 AWS 보안 그룹 설정은 모든 포트에 열려 있는것으로 추정
  (cli를 통해 확인 가능한데 확인하기 위해 AWS CLI를 다운받아야 되는데 메모리 잡아먹는다고 해서 안깔음)

- 그래서 그냥 서버 내 ufw 포트만 열어두면 됨

- **실행 과정**

- "/var/lib/docker/volumes/mariadb_volume/_data",

- my_mariadb_volume:/var/lib/mysql

  - **컨테이너 실행**

    - ````bash
      docker run -d --name mariadb \
      -v mariadb_volume:/var/lib/mysql \
      -e TZ=Asia/Seoul \
      -e MYSQL_ROOT_PASSWORD=my-secret-pw \
      -p 3306:3306 mariadb:latest
      ````
      
      - `-d`: 컨테이너를 백그라운드 모드로 실행합니다. 즉, 터미널이 컨테이너 실행에 묶이지 않고 즉시 프롬프트로 돌아옵니다.
      - `--name mariadb`: 실행할 컨테이너에 `mariadb`라는 이름을 지정합니다. 이 이름을 사용하여 컨테이너를 참조하거나 관리할 수 있습니다.
      - `-v mariadb_volume:/var/lib/mysql`: `mariadb_volume`이라는 볼륨을 생성하거나 사용하여 컨테이너의 `/var/lib/mysql` 디렉토리에 마운트함.`my_mariadb_volume` 볼륨이 없는 경우 자동으로 생성하고 마운트 합니다.`/var/lib/mysql`의 경우 MariaDB 컨테이너가 실행 될 때 내부적으로 생성됨
      - `-e TZ=Asia/Seoul` : 컨테이너의 환경 변수를 설정하는 데 사용. 환경 변수를 통해 컨테이너의 시간대(time zone)를 설정. 컨테이너의 시간대를 설정한다는 것은 컨테이너 내부에서 실행되는 모든 프로세스와 서비스의 시간대를 설정하는 것을 의미.
      - `-e MYSQL_ROOT_PASSWORD=my-secret-pw`: MariaDB 루트 계정의 비밀번호를 `my-secret-pw`로 설정합니다. 이 환경변수는 MariaDB 컨테이너를 처음 실행할 때 필수입니다. `MYSQL_ROOT_PASSWORD`이름은 환경 변수이기 때문에 환경변수 설정을 변경할 게 아니라면 이름을 바꾸면 안됨.
      - `-p 3306:3306`: 호스트의 3306 포트와 컨테이너의 3306 포트를 바인딩합니다. 이렇게 함으로써 호스트 머신의 해당 포트를 통해 MariaDB에 접근할 수 있습니다.
      - `mariadb:latest`: 사용할 이미지를 지정합니다. 여기서는 `mariadb`의 `latest` 태그를 사용하여 최신 버전의 MariaDB 이미지를 사용합니다.
    
  - **스키마 생성**
  
    - 백엔드 서버에서 DB서버에 접근하기 위해서는 DB에 스키마를 만들어 놔야 됨. Spring Boot와 같은 일부 프레임워크에서는  테이블을 자동으로 생성하는 기능을 제공하지만, 스키마의 자동 생성은 대부분 지원 범위 바깥에 있음 (수동으로 만들어야 됨)
  
    - ```bash
      docker exec -it my_mariaDB mariadb -uroot -p
      ```
  
      - `docker exec`: 실행 중인 Docker 컨테이너 내부에서 명령어를 실행하도록 Docker에 지시합니다.
      - `-it`
        - `-i` (혹은 `--interactive`): 컨테이너의 표준 입력(STDIN)을 열어서 대화형 터미널로 사용할 수 있도록 합니다.
        - `-t` (혹은 `--tty`): 가상 터미널(TTY)을 할당합니다. 이는 사용자가 컨테이너의 쉘에 입력하고 결과를 볼 수 있게 하는데 필요합니다.
      - `my_mariadb`: 실행할 명령어가 동작할 대상 컨테이너의 이름
      - `mariadb -uroot -p`
        - `mariadb`: MariaDB 서버에 접속하기 위한 명령줄 도구입니다.
        - `-uroot`: `root` 사용자로 로그인하라는 의미입니다. `-u`는 사용자 이름을 지정하는 옵션입니다. (-u랑 root 띄어쓰기 안하는게 맞음)
        - `-p`: 이 옵션은 명령어 실행 시 비밀번호 입력을 요청하도록 합니다. 실제 비밀번호는 명령어에 포함하지 않고, `-p` 옵션 사용 후 대화형 프롬프트에서 입력합니다. 
          (비밀번호 같이 입력하려면 -ppassword, 이것도 -p랑 password 띄어쓰기 안하는게 맞음)
      
    - `CREATE DATABASE your_database_name;` : 스키마 생성
    
    > Query 명령어
    >
    > - Create : 생성
    >
    > - SHOW : 조회
    >
    > - USE : 사용
    >
    > ex)
    >
    > 1. 스키마 목록 확인 : `SHOW DATABASES;`
    >
    > 2. 스키마에 존재하는 테이블 확인 
    >
    >    ```bash
    >    USE your_database_name;
    >    SHOW TABLES;
    >    ```
    >
    > 3. 테이블에 존재하는 데이터 확인
    >
    >    - 2번  + `SELECT * FROM your_table_name;` 