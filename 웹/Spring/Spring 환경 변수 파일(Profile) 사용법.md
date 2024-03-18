# Spring 환경 변수 파일(Profile) 사용법

### 1. 프로파일(Profile) 기반 설정 파일 사용하기

`application.properties` 혹은 `application.yml` 파일에서 `spring.profiles.include`를 사용하는 것은 특정 프로파일을 활성화할 때 함께 활성화될 추가 프로파일들을 지정합니다. 예를 들어, ``라고 설정하면, Spring은 `db`와 `env` 프로파일을 함께 활성화합니다. 이는 특정 프로파일이 활성화되었을 때 추가적으로 필요한 프로파일들을 자동으로 포함시키고 싶을 때 유용합니다.

### 2. 파일 이름 구조

- `application-{profile}.properties` (또는 `.yml`) 형식. 여기서 `{profile}`은 활성화하려는 프로 파일의 이름. (`spring.profiles.include= {profile}`에 사용되는 프로파일 이름임)

  - `application.properties`: 모든 환경에 대한 공통 설정을 포함합니다.

  - `application-db.properties`: `db` 프로파일에 특화된 설정을 포함합니다.

  - `application-env.properties`: `env` 프로파일에 특화된 설정을 포함합니다.


### 3. 사용 예시

- `application-env.properties`에  환경 설정에 특화된 설정을 정의합니다.

  ```properties
  SPRING_DATASOURCE_URL= xxxx
  SPRING_DATASOURCE_USERNAME= xxx
  SPRING_DATASOURCE_PASSWORD= xxx
  ```

- `application.properties`에서 프로파일에 정의된 변수를 사용합니다.

  - `application.properties` 파일 내에서 다음과 같이 프로파일을 지정하고 프로파일에 작성된 변수명 사용

  ```properties
  spring.profiles.include=db,env
  spring.datasource.url=${SPRING_DATASOURCE_URL}
  ```

