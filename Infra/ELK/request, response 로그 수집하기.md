# RequestLoggingFilter로 request, response 로그 수집하기

### 0. 참고

- https://keichee.tistory.com/467
- https://keichee.tistory.com/468

---

### 1. 개요

- 우선 request와 response 로그를 수집하려면 해당 로그를 출력을 해야됨
- Spring의 전역 로깅 설정은 서비스 내부의 비즈니스 로직 실행 중에 발생하는 로그를 기록함. **하지만 필터는 비즈니스 로직에 진입하기 전의 요청 상태나 응답 후의 최종 상태를 기록할 수 있기 때문에, 애플리케이션 레벨에서 잡히지 않는 정보를 수집하는 데 유리함.**
- 결국 위 2가지 이유로 request와 response를 로그를 수집하는 필터를 만들어서 필터체인에 등록해야 됨



### 2. AbstractRequestLoggingFilter

- **AbstractRequestLoggingFilter는 Spring에서 제공하는 필터 중 하나로, HTTP 요청에 대한 로깅을 자동으로 수행하는 기능을 제공**
- 이 필터를 사용하면 HTTP 요청 정보(예: 요청 URI, HTTP 메서드, 요청 파라미터 등)를 자동으로 로그로 남길 수 있음
- **주요 역할**
  - `RequestLoggingFilter`는 들어오는 HTTP 요청을 가로채 다음과 같은 정보를 로그로 기록할 수 있음
    - 요청 URI, HTTP 메서드, 파라미터, 요청 바디, 헤더 정보 등



### 3. AbstractRequestLoggingFilter 구현 방법

1. **`AbstractRequestLoggingFilter`의 구현체인 `RequestLoggingFilter` 클래스 작성**

   ```java
   package com.drive.sidepjt.common.filter;
   
   import lombok.extern.slf4j.Slf4j;
   import org.springframework.web.filter.AbstractRequestLoggingFilter;
   import jakarta.servlet.http.HttpServletRequest;
   
   @Slf4j
   public class RequestLoggingFilter extends AbstractRequestLoggingFilter {
   
       @Override
       protected void beforeRequest(HttpServletRequest request, String message) {
           logger.info(message);
       }
   
       @Override
       protected void afterRequest(HttpServletRequest request, String message) {
           logger.info(message);
       }
   }
   ```

2. **`Bean` 등록 및 `Filter` 세부 설정**

   - **필터**는 기본적으로 **스프링 컨텍스트에 빈(Bean)으로 등록**되면, **자동으로 필터 체인에 포함 됨**
   - 이때 명시적 우선순위 설정 없이 필터를 등록하면, Spring Boot는 필터를 체인의 마지막 부분에 등록되므로, 해당 필터는 가장 앞쪽에 위치해야 되기 때문에 `@Order(1)`을 설정해 필터가 가장 앞쪽에 등록되도록 설정

   ```java
   @Configuration
   public class RequestLoggingConfig {
   
       public static int MAX_PAYLOAD_LENGTH = 1000;
   
       @Bean
       @Order(1)
       public RequestLoggingFilter requestLoggingFilter() {
           RequestLoggingFilter filter = new RequestLoggingFilter();
           filter.setIncludeClientInfo(true);
           filter.setIncludeHeaders(false);
           filter.setIncludePayload(true);
           filter.setIncludeQueryString(true);
           filter.setMaxPayloadLength(MAX_PAYLOAD_LENGTH);
           return filter;
       }
   }
   ```

3. **logback.yml에 추가**

   ```yml
   <?xml version="1.0" encoding="UTF-8"?>
   
   <configuration>
   
       <include resource="org/springframework/boot/logging/logback/defaults.xml"/>
       <include resource="org/springframework/boot/logging/logback/console-appender.xml"/>
   
       <springProfile name="local">
           <logger name="com.keichee.demo" level="debug" additivity="false">
               <appender-ref ref="CONSOLE"/>
           </logger>
   
           <root level="error">
               <appender-ref ref="CONSOLE"/>
           </root>
       </springProfile>
   
   </configuration>
   ```

### 4. 참고

- **CommonsRequestLoggingFilter**

  - Spring은 **`CommonsRequestLoggingFilter`**라는 구현체를 제공하여, **간단하게 요청 정보를 로그로 남길 수 있도록** 지원

  - 이 필터는 요청이 들어올 때 **URI**, **HTTP 메서드**, **쿼리 파라미터** 등 **요청에 관한 주요 정보를 자동으로 기록**해 줌

  - 요청 바디(payload)까지 로깅하도록 설정할 수 있음

- **CommonsRequestLoggingFilter vs AbstractRequestLoggingFilter** 

  | **특징**            | **CommonsRequestLoggingFilter (Spring 제공)**   | **AbstractRequestLoggingFilter(직접 구현)**         |
  | ------------------- | ----------------------------------------------- | --------------------------------------------------- |
  | **사용 용이성**     | 매우 간단하게 설정 가능                         | 직접 구현 필요                                      |
  | **기능 제공**       | 기본적인 HTTP 요청 정보 로깅                    | 원하는 방식으로 로깅 가능, 완전한 커스터마이징 가능 |
  | **설정 방식**       | 메서드를 통해 설정 (IncludeHeaders, Payload 등) | 직접 코드로 작성                                    |
  | **유연성**          | 제공된 기능을 넘어서는 확장 어려움              | 완전히 자유로운 커스터마이징 가능                   |
  | **복잡한 요구사항** | 기본적이고 단순한 로깅에 적합                   | 복잡한 로깅 시나리오에 적합                         |