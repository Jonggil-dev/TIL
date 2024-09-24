# Drive 사이드 프로젝트 에러 정리



### 0. 자소서 소스

- **실무에서 Exception 처리**
  - 원래 개발 단계에서는 에러가 발생하는 부분에 대해 디버깅을 위해 명시적으로 예외를 던졌음
  - **근데 실무에서는 무조건 예외를 던질게 아니라 사용 환경을 고려해 논리적으로 따져서 에러 로그만 찍고 다음 로직으로 넘어갈 필요도 있음**
    - 하나의 메서드에서 하나의 동작만 하는 경우나 메인 로직 등 디버깅을 위해 필수적인 경우 예외를 던져도 괜찮음 -> 어짜피 에러가 나면 해당 동작을 못하게 되는거니까
    - 그런데, 하나의 메서드에서 여러 동작이 있거나, 아니면 여러 사용자에 대해 동작하는 경우에 메서드 특정 부분에서 예외를 던져버리면,
      이후의 동작이나, 다른 정상 사용자의 동작까지 멈춰버림
      - 예시로 FCM 토큰으로 하나의 메서드에서 5명의 유저에게 알림을 보내려고 하는데, 3번째 유저에서 에러가 발생했을 경우 예외를 던져버리면 4, 5번째 유저는 정상적으로 알림을 받을 수 있는 유저라도 알림 기능이 멈춰버림
- **트렌젝션 관리**
  -  학원 차량을 통해 애플리케이션의 베타 테스트를 진행하던 중 배차 목록 데이터의 불일치 문제가 발생
  - MongoDB와 Postgres의 데이터를 순차적으로 삭제하는 로직에서 오류가 발생하였으나, MongoDB의 롤백이 실행되지 않았었음
  - Spring에서 NoSQL과 RDBMS의 트랜잭션이 별도로 관리된다는 것을 알게 됨
  - 또한, 트랜잭션 하나하나가 서비스 안정성과 직결된다는 점을 깨닳음
- **로그 모니터링**
  - 실제 서비스에서는 문제를 신속하게 파악하고 대응하는 것이 중요하기에 로그 모니터링은 선택이 아닌 필수라는 것을 깨달음. 
  - 추가로, 로그를 통해 유저의 사용 패턴이나 사용률 등을 분석 가능.
  - 정리
    - 사용자 행동 분석 로그 데이터를 통해 사용자의 행동 패턴을 파악
    - 오류 및 버그 추적 로그를 통해 시스템 내 발생한 오류나 문제를 확인하고 추적
    - 성능 최적화 로그 데이터를 분석하여 데이터 베이스 작업, API 호출, 시스템 리소스 사용 등 시스템의 성능 상태를 파악
    - 보안 감시 로그를 통해 시스템 내의 비정상적인 활동, 해킹 시도, 인증 실패 등을 식별하고, 보안 취약점 파악
  - 이에 현재 ELK 스택을 활용해 로그를 수집하고 시각화하는 것을 단기 목표로 함

---



### 1. Spring과 DB 연결시 Schema 이름 설정 문제

- Postgres는 구조가 데이터베이스명 -> schema 명 -> table 이런식으로 되어 있음

- Postgres는 데이터베이스를 생성할 때 기본적으로 public이라는 schema가 생성되고, **Spring 연결시에도 schema에 관한 정보를 명시하지 않으면 public schema에 대해 쿼리를 사용**

- 하지만, 본 프로젝트에서는 sidepjt라는 schema를 만들고 연결하는 과정에서 schema를 지정해주지 않아 에러가 발생

  - 아래와 같이 url의 뒤쪽에 shcemaName을 추가하면서 해결 
    **(jpa hibernate, schema.sql 파일을 이용한 ddl 방식 모두 아래 설정 하나면 적용 됨)**
  
    ```yml
    spring:
    	datasource:
    		url: jdbc:postgresql://localhost:5432/DataBaseName?currentSchema = schemaName
    ```



### 2. Schema.sql의 CREATE TABLE, DROP TABLE 순서 문제

- Schema.sql에 작성된 TABLE 중 외래키를 사용하는 테이블이 있을 경우 CREATE TABLE, DROP TABLE 의 순서가 잘못 되면 에러가 발생

  - 생성 시 : 참조가 되는 TABLE -> 참조를 하는 TABLE 순서로 생성
  - 삭제 시 : 참조를 하는 TABLE -> 참조가 되는 TABLE 순서로 삭제

  

### 3. DB의 Password 타입을 CHAR로 했을 때 문제점

- 회원가입 및 로그인에서 비밀번호는 Bcrypt 해시 알고리즘을 사용하는 PasswordEncoder를 사용하고 있는 상황이었음
- 그런데 로그인 인증 과정 중 **`Encoded password does not look like BCrypt`** 에러 발생

- **디버깅**

  - 원인 :  **해시 알고리즘을 통하면 해싱된 값의 길이가 일정하기 때문에, DB에 Password 필드의 데이터 타입을 CHAR(70)으로 설정해 두었음**
    - `CHAR` 타입은 고정 길이 문자열을 저장하기 때문에 해시된 비밀번호의 길이가 DB의 제한 글자수보다 작다면 나머지가 공백으로 채워짐
    - 이로 인해, 로그인 과정에서 request의 비밀번호를 해싱한 값과 **DB에 저장된 비밀번호를 비교하는 과정에서 공백 문자열로 인해 에러를 초래**

  - 해결책 : **DB에 Password 필드의 데이터 타입을 VARCHAR로 변경하여 해결함**



### 4. JwtAuthentiation 401 -> 500 에러 반환 문제

- ```java
      @Override
      public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
          if (!request.getMethod().equals("POST")) {
              (1) throw new BadRequestException("testtest");
              (2) throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
          }
  ```

  - 위 코드에서 (1) 번으로 처리했을 경우 400 -> 500 으로 변환 돼서 응답이 return 되고 (2) 의 경우는 정상적으로 401으로 응답이 return 됨
  - 로그를 분석해보면, 두 가지 경우 모두 에러가 발생하고 /error로 톰캣에서 리다이렉션이 수행됨
  - 401의 경우 FilterChain 내에서 해당 예외를 처리하는 곳이 어딘가 있어서 그대로 /error 리다이렉션이 진행이 된거고,
  - 400의 경우 FilterChain 내에서 해당 예외를 처리하는 곳이 없어서 500에러로 승격되고 그 상태로 /error 로 리다이렉션이 진행된 것으로 추측함



### 5. 인텔리제이 Build 시 .jar 파일 실행 에러

- 인텔리제이로 `Project Settings -> Artifacts -> + -> jar ->  From modules with ...` 누르고 MainClass 지정한 뒤 `상단 탭 -> Build -> Build Artifact` 를 실행해서 나온 .jar 파일을 사용하면 자꾸 `no main manifest attribute` 에러가 발생함
  - 인터넷에 있는 해결방법 전부 시도해봤는데 해결이 안됨
- 결과적으로 jar 파일 내부에는 jar 파일에 대한 메타데이터를 포함하는 `Manifest` 파일이라는 것이 있음 . 이 파일은 JAR 파일을 실행할 때 어떻게 동작할지를 정의하는 정보를 제공 해줌.
- 그런데 위의 `no main manifest attribute` 는 해당 `Manifest`파일에 Main 클래스에 대한 정보가 누락되어서 발생하는 오류임. 실제 jar파일 내부의 `MANIFEST.MF` 을 열어보면 Main-Class 에 대한 정보가 없음. 그냥 인텔리제이 버그 같음
- 그래서 인텔리제이 말고 터미널 명령어 `./gradlew build` 를 통해 gradle로 빌드를 하니까 해당 에러 해결됨 -> `/build/lib` 폴더에 jar파일 생성됨



### 6. Nginx http -> https redircetions 시 에러

- Nginx config에 80 포트에 대해 `return 301 https://$host$request_uri;` 을 하여 443 포트로 redirection 을 설정해 놓았음
- 그런데 POST 요청으로 `http://도메인/api/users/teacher` 요청을 보냈는데 응답이 401에러가 자꾸 발생
  (해당 요청은 permitall() URL)
- request를 추적해 보니 POST `http://도메인/api/users/teacher`가  GET `https://도메인/api/users/teacher`로 redirect 되고 있었음
- GPT에 물어보니까 `return 301`은 표준이 GET 요청으로 리다이렉트 하는거라 `return 307 or 308`을 사용하면 기존의 Http method를 유지한 상태로 redirect 가능 -> 설정하니까 해결됨



### 7. transactionManager 충돌 에러

- 기본적으로 JPA의 `@transactional`은 `PlatformTransactionManager`를 사용하고 RDBMS에만 적용됨

- MongoDB도 트렌젝션을 추가 하기 위해 별도의 `MongoTransactionManager`를 Bean으로 등록 함

- 그런데 `@Transactional`을 사용하는 메서드에서 transactionManger 충돌 에러가 발생함

- `@Transactional` 에서 `TransactionManager`가 2개라 JPA 입장에서 어떤 거를 사용해야 될 지 모르겠다는 뜻

- ```java
  @Configuration
  public class BeanConfig {
  
      @Bean
      @Primary
      public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
          return new JpaTransactionManager(entityManagerFactory);
      }
  
      @Bean
      public MongoTransactionManager mongoTransactionManager(MongoDatabaseFactory dbFactory) {
          return new MongoTransactionManager(dbFactory);
      }
  
  }
  ```

  이렇게 `PlatformTransactionManager`에 `@Primary`를 추가하여 기본적으로 `@Transactional` 어노테이션에서는 `PlatformTransactionManager`를 우선 사용하도록 설정

- `MongoTransactionManager`가 필요한 부분에서는 해당 `MongoTransactionManager`타입을 명시적으로 세팅해서 사용하는 방법으로 해결 함

  ```java
   public class CarScheduleService {
   	private final MongoTransactionManager mongoTransactionManager;
  
      public void saveCarSchedule(int userId, CarScheduleListRequestDto carScheduleListRequestDto) {
  	TransactionTemplate transactionTemplate = new TransactionTemplate(mongoTransactionManager);
  	transactionTemplate.execute(status -> {
              for (VehicleRequestDto vehicleDto : vehicles) {
              ...
  	}
  
  ```

  

### 8. AWS에서의 Spring - Postgres 연결 문제 ( spring.jpa.hibernate.dialect + localhost 문제)

- AWS 상황 1 (문제 없이 정상 동작)
  - **Spring 서버 : .jar 파일 실행 (도커 컨테이너 X)**
  - DB(Postgres, Redis, MongoDB) : 도커 컨테이너 실행
  - Spring properties 설정
    - spring.jpa.hibernate.dialect 설정 X
    - postgres 주소 설정
      -  spring.datasource.url : jdbc:postgresql://localhost:5432/drive?currentSchema=sidepjt
    - Redis 주소 설정
      - spring.data.redis.host : localhost
- 문제
  - **Spring 서버를 도커 이미지로 만들어 컨테이너로 실행하려고 하니**
    1. hibernate.dialect를 찾지 못했다는 에러 발생 
       - **properties에 spring.jpa.hibernate.dialect 설정 추가하여 해결**
    2. Postgres와 Redis의 연결에 실패했다는 에러 발생
       - **DB 주소를 localhost에서 도메인으로 바꾸어 해결**
- 정리
  - **.jar 파일을 aws에서 실행할 때는 도커가 아니므로 localhost 환경이 aws 서버 자체가 해당 됨**
  - **하지만, AWS 환경에서 도커 컨테이너로 실행할 경우에는 localhost 환경이 도커 컨테이너 내부에만 적용됨**
  - **즉, Spring 서버 입장에서 Spring 컨테이너 내부가 localhost 이므로 다른 DB와의 연결을 위해서는 AWS의 도메인으로 주소를 설정해서 연결 해야 됨**



### 9. 실무에서 에러시 예외처리

- 평소 개발 과정에서는 예외 상황에 대해 에러를 던지며 메서드의 실행을 중단 시켰음

- **하지만, 베타 테스트를 하며 실제 상황에서는** 

  - **하나의 메서드에서 하나의 동작만 하거나, 핵심 동작에서 에러가 터지는 부분에 대해서는 동작을 중지하고 에러를 반환하는게 맞아보임**

  - **하지만 하나의 메서드에서 여러 동작이 묶여있는 경우 -> 결국 상황에 맞게, 논리적인 판단 하에, 실패 시에도 에러 로그를 남기고 다음 동작으로 넘어 갈 수 있도록 구성을 해야 될 필요도 있음 (아래처럼 Runnable action 타입 사용)**

    ```java
    package com.drive.sidepjt.common.util;
    
    import lombok.extern.slf4j.Slf4j;
    
    @Slf4j
    public class SafeActionUtils {
        public static void safeExecute(Runnable action, String errorMessage) {
            try {
                action.run();
            } catch (Exception e) {
                log.error(errorMessage, e);
            }
        }
    }
    ```

    - 예시

      - **FCM을 사용한 에러 처리 부분에서 단체 FCM 알림을 보내는 도중 한 부분에서 실패하니 뒤에 로직이 실행이 안됨**
      - **가능한 유저들에 대해서는 정상적으로 처리가 필요함**  

      

### 10. Scheduler에서 엔티티의 Lazy Loading 필드에 접근할 때 문제 

- 매일 새벽 `Boarding_List`의 Base 레코드를 만들기 위한 과정에서,  `Scheduler`로 `CarScheduleStudentMapping`의 `User` 엔티티의 `getUsername`에 접근하려 했음.
- 하지만, 스케줄러가 돌아가면서 `User` 엔티티의 `getUsername`에 접근하는 도중 에러가 발생
- **당시 `CarScheduleStudentMapping`의 `User` 엔티티는 `Lazy Loading`을 설정해 놨었는데 이게 문제가 됨**
  - **결론 : `Scheduler`의 메서드 별로 `@Transactional` 어노테이션 추가 하여 해결** 
  - `@Scheduled` 메서드는 기본적으로 트랜잭션이 포함되어 있지 않기 때문에, 데이터베이스와의 세션이 유지되지 않음. 그래서 Hibernate에서 지연 로딩(Lazy Loading)을 사용하는 엔티티 필드는 실제로 접근할 때 데이터베이스에서 조회하는데, 트랜잭션이 없으면 데이터베이스 세션이 종료된 상태이므로, 지연 로딩 시 `LazyInitializationException`이 발생합니다.
  - 그런데 여기서, `Service `계층에서 `Lazy Loading` 엔티티의 필드에 접근할 때는 해당 문제가 없었음
    - 지피티 답변
      - Spring 애플리케이션에서 요청이 들어오면, Spring Data JPA는 자동으로 데이터베이스와의 세션을 열어, 엔티티를 로드하고 그 상태를 유지할 수 있습니다. 세션이 열려 있는 동안에는 지연 로딩이 가능한 상태가 되며, 트랜잭션이 없더라도 엔티티는 조회할 수 있습니다. -> **즉, Service에서는 JPA 가 자동으로 데이터 베이스 세션을 열어줌**
      - 이 때 데이터 베이스 세션과 트랜젝션이랑은 다른 개념임
        - **@Transactional이 없을 때**: 데이터베이스 세션은 열릴 수 있지만, 트랜잭션은 시작되지 않습니다. 따라서 엔티티 조회는 가능하지만, 에러 발생 시 자동 롤백이 되지 않고 수동으로 처리해야 합니다.
        - **@Transactional이 있을 때**: 트랜잭션이 활성화되고, 에러가 발생하면 자동으로 롤백됩니다.
