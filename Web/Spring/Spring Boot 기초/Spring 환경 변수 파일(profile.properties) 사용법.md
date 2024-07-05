# Spring 환경 변수 파일(profile.properties) 사용법 2가지

### 참고. profile 파일 이름 구조

- `application-{profile}.properties` 형식. 여기서 `{profile}`은 활성화하려는 프로 파일의 이름. (`spring.profiles.include= {profile}`에 사용되는 프로파일 이름임)
  - `application.properties`: 모든 환경에 대한 공통 설정을 포함합니다.
  - `application-env.properties`: `env` 프로파일에 특화된 설정을 포함합니다.

### 1. spring.profiles.include

- `spring.profiles.include` 속성은 이미 활성화된 프로파일에 추가로 포함시킬 프로파일을 지정.
- 이 속성은 주로 특정 프로파일에 대한 공통 설정을 다른 프로파일과 함께 사용하고자 할 때 유용 함.
- 예를 들어, `application.properties`에서 `prod` 프로파일을 활성화했고, 여기에 추가적으로 `db-prod` 설정을 적용하고 싶다면, `prod` 프로파일 설정 내에서 `spring.profiles.include=db-prod`라고 지정할 수 있습니다.

- 결국, `aplication.properties`에  `spring.profiles.include=env`라고 설정하면, Spring은 `application.properties` 파일은 런타임 환경에서 기본적으로 활성화 되므로 `env` 프로파일을 함께 활성화합니다. 

  - `application-env.properties`에  환경 설정에 특화된 설정을 정의.

    ```properties
    SPRING_DATASOURCE_URL= xxxx
    SPRING_DATASOURCE_USERNAME= xxx
    SPRING_DATASOURCE_PASSWORD= xxx
    ```

  - `application.properties`에서 프로파일에 정의된 변수를 사용

    - `application.properties` 파일 내에서 다음과 같이 프로파일을 지정하고 프로파일에 작성된 변수명 사용

    ```properties
    spring.profiles.include=env
    spring.datasource.url=${SPRING_DATASOURCE_URL}
    ```

### 2. spring.profiles.active

- `spring.profiles.active` 속성은 애플리케이션을 **실행할 때** 활성화할 프로파일을 지정
- 여러 프로파일을 동시에 활성화하려면 쉼표로 구분하여 나열할 수 있음
- 예를 들어, `spring.profiles.active=dev,db-h2`라고 설정하면 `dev`와 `db-h2` 프로파일이 모두 활성화 됨
- 애플리케이션의 `application.properties`파일에 지정 or 명령줄 인자 or 환경 변수를 통해서도 지정하는 3가지 방법이 있음
  - `application.propertie`s 파일에 지정하는 방법 : `spring.profiles.active=prod`
  - 명령줄 인자 
    -  `java -Dspring.profiles.active=prod -jar yourapp.jar`
    - 인텔리제이의 경우 `실행 -> 구성편집 -> 옵션수정 -> VM옵션 추가 -> -Dspring.profiles.active=prod`에 작성하고 실행하면  `java -Dspring.profiles.active=prod -jar yourapp.jar`이거랑 동일한 효과임
  - 환경 변수 : 인터넷 참고

### 3. spring.profiles.include vs spring.profiles.active

- **활성화 시점의 차이**: `spring.profiles.active`는 **애플리케이션을 시작할 때 외부에서 주로 지정하는 방법(예: 환경 변수, JVM 시스템 프로퍼티, 커맨드 라인 인자)으로 활성화할 프로파일을 결정**합니다. 반면, `spring.profiles.include`는 이미 활성화된 프로파일의 설정 파일 내부에서 추가적으로 다른 프로파일을 포함시키는 방식입니다.
- **사용 목적의 차이**: `spring.profiles.active`는 실행 환경에 맞는 주 프로파일을 선택하는 데 사용되며, `spring.profiles.include`는 특정 프로파일에 대한 추가 설정이 필요할 때 사용됩니다. 즉, `include`는 보통 공통적인 설정을 재사용하고자 할 때 유용하게 사용됩니다.
