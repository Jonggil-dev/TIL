# Docker

### 1. Docker

- Docker 컨테이너를 관리하기 위한 도구로 일종의 프로그램

### 2. Docker Container

- 한 대의 서버에서 여러 개의 소프트웨어를 안전하고 효율적으로 운영할 수 있도록 하는 도구
- 애플리케이션이 실제로 실행되는 격리된 환경

### 3. Docker Image

- 애플리케이션을 실행하는 데 필요한 모든 파일과 설정을 포함하는 불변의 스냅샷
- 컨테이너를 생성하는 기반이 되는 파일
- 도커 이미지는 여러 개의 읽기 전용 레이어로 구성. 각 레이어는 이미지를 만드는 과정에서 발생한 변경 사항들을 포함. 예를 들어, 하나의 레이어에는 운영 체제가, 다른 레이어에는 애플리케이션이, 또 다른 레이어에는 애플리케이션 설정이 포함

### 4. DockerFile

- 텍스트 파일 형식으로, Docker 이미지를 빌드하는 데 필요한 지시사항을 포함

- `docker build -t 이미지명:태그` 명령어를 통해  해당 디렉토리의 `Dockerfile`을 기본으로 인식하여 이미지를 빌드

- ```txt
  # DockerFile 코드 예시
  
  FROM python:3.8
  WORKDIR /app
  COPY . /app
  RUN pip install -r requirements.txt
  CMD ["python", "app.py"]
  ```

### 4. Docker Registry

- Docker Image를 저장하고 공유하는 서비스
- Docker Hub가 가장 잘 알려진 공개 레지스트리

### 5. 쿠버네티스

- 서버가 여러 대 있는 환경에서 각각의 서버의 도커에게 대신 지시해주는 오케스트레이션 도구