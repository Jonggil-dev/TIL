# Docker Compose

### 1. 개념 (출처 https://seosh817.tistory.com/387)

- 여러 개의 컨테이너를 하나의 서비스로 정의해 컨테이너의 묶음으로 관리할 수 있는 작업 환경을 제공하는 관리 도구

- 여러 개의 컨테이너가 하나의 어플리케이션으로 동작할 때 도커 컴포즈를 사용하지 않는다면, 이를 테스트 할 때 각 컨테이너를 하나씩 생성 및 실행 해야 됨 
  -> Docker Compose를 사용하면 docker-compose.yml` 파일에 정의된 모든 서비스(컨테이너)를 한 번에 생성하고 실행할 수 있음

  > 참고
  >
  > - 컨테이너는 기술의 특성상 컨테이너 인스턴스를 "생성"만 해놓고 "실행"하지 않는 것은 일반적인 사용 사례가 아님
  > - `docker run`은 각 컨테이너를 생성한 뒤 실행까지 연속적으로 수행하는 명령어임
  >   - 생성과 실행을 분리하려면 `docker create`와 `docker start` 명령어를 사용하면 됨