# Drive 사이드 프로젝트 에러 정리

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