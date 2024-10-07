# LoggingFilter로 request, response 로그 수집하기

### 0. 참고

- 채택 안함
  - [AbstractRequestLoggingFilter를 이용한 요청, 응답 로깅 1 / 2](https://keichee.tistory.com/467)
  - [AbstractRequestLoggingFilter를 이용한 요청, 응답 로깅 2 / 2](https://keichee.tistory.com/468)

- 채택 방법 : GPT 참고

---

### 1. 개요

- 우선 request와 response 로그를 수집하려면 해당 로그를 출력을 해야됨
- Spring의 전역 로깅 설정은 서비스 내부의 비즈니스 로직 실행 중에 발생하는 로그를 기록함.
  **하지만 필터는 비즈니스 로직에 진입하기 전의 요청 상태나 응답 후의 최종 상태를 기록할 수 있기 때문에, 애플리케이션 레벨에서 잡히지 않는 정보를 수집하는 데 유리함.**
- 결국 위 2가지 이유로 request와 response를 로그를 수집하는 필터를 만들어서 필터체인에 등록해야 됨

- **그리고 해당 TIL의 설정들은 `로깅 전략 TIL`의 전략되로 세팅이 된 거 임**

### 2. 고려 사항

- **`LoggingFilter`는 `OncePerRequestFilter`를 상속받아 구현**

- **Spring에는 request 로깅을 할 수 있도록 간편하게 제공하는`AbstractRequestLoggingFilter`의 구현체들이 있지만 아래와 같은 이유로 사용 안함**

  - **응답 로깅 미지원** : `AbstractRequestLoggingFilter`는 오직 HTTP 요청만 로깅할 수 있음

  - **요청 본문 재사용 문제**

    - 요청 본문은 스트림으로 처리되며, 한 번 읽으면 소모됨
    - `AbstractRequestLoggingFilter`는 요청 본문을 캐싱하지 않으므로, 필터에서 본문을 읽으면 이후 컨트롤러에서 본문을 읽을 수 없게 됨

  

### 3. `LoggingFilter`를 통한 Request, Response 로그 수집

**1. `LoggingFilter` 구현**

- **`ContentCachingRequestWrapper`, `ContentCachingResponseWrapper`**
  - `HttpServletRequest`를 래핑하여 요청, 응답 본문을 캐싱
  - 요청, 응답 본문을 여러 번 읽을 수 있게 해줌

  - **이때 응답 본문 캐싱 `ContentCachingResponseWrapper`를 사용한 경우 `copyBodyToResponse()` 메서드를 호출하여 실제 응답에 내용을 복사 하는 작업이 필수임!**
    - `ContentCachingResponseWrapper`를 사용하지 않은 경우
      - `HttpServletResponse`를 통해 데이터를 즉시 클라이언트에게 전송함
    - **`ContentCachingResponseWrapper`를 사용한 경우**
      - 응답 데이터를 즉시 보내지 않고, 내부에 저장 (캐싱)
      - 캐싱된 데이터를 클라이언트에게 보내려면 `copyBodyToResponse()`를 통해 캐싱된 응답 데이터를 실제 클라이언트에게 복사해서 전송해야 함
- **로깅 메시지의 경우 `logstash`에서 전처리하여 `Elasticsearch`에 인덱싱 되도록 하기 위해 Map 자료구조를 사용해 JSON 형태로 출력 함**
-  **트랜잭션 ID의 경우 로그 추적 시 request ~ response를 추적하기 쉽도록 관리하기 위해 UUID로 생성하여 반영함**
  - **MDC(Mapped Diagnostic Context)에 값을 설정하면, Logback이나 SLF4J 같은 로깅 프레임워크는 MDC에 저장된 값을 자동으로 포함해서 로그를 기록하도록 설계되어 있기 때문에 별도로 log로 출력을 찍지 않아도 됨**


```java
package com.drive.sidepjt.common.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapㄴper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
public class LoggingFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(LoggingFilter.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        ContentCachingRequestWrapper cachingRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper cachingResponse = new ContentCachingResponseWrapper(response);

        try {
            // 트랜잭션 ID 생성 후 MDC에 추가
            MDC.put("transactionId", generateTransactionId());

            // 필터 체인 실행
            filterChain.doFilter(cachingRequest, cachingResponse);
        } finally {
            // 요청 및 응답 로깅
            logRequestDetails(cachingRequest);
            logResponseDetails(cachingResponse);

            // 캐시된 응답 데이터를 실제 응답으로 복사
            cachingResponse.copyBodyToResponse();

            // 요청 완료 후 MDC 값 제거
            MDC.clear();
        }
    }

    // 트랜잭션 ID 생성 메서드
    private String generateTransactionId() {
        return UUID.randomUUID().toString();  // 고유한 UUID 생성
    }

    private void logRequestDetails(ContentCachingRequestWrapper request) {
        try {
            Map<String, Object> logMap = new HashMap<>();
            logMap.put("method", request.getMethod());
            logMap.put("uri", request.getRequestURI());
            logMap.put("query_string", request.getQueryString());
            logMap.put("client_ip", request.getRemoteAddr());
            logMap.put("authentication_header", request.getHeader("Authorization") != null ? request.getHeader("Authorization") : "[No Authentication Header]");
            logMap.put("request_body", getRequestBody(request));

            // JSON 형식으로 요청 로그 기록
            logger.info(objectMapper.writeValueAsString(logMap));
        } catch (Exception e) {
            logger.error("Error logging request", e);
        }
    }

    private void logResponseDetails(ContentCachingResponseWrapper response) {
        try {
            Map<String, Object> logMap = new HashMap<>();
            logMap.put("status", response.getStatus());
            logMap.put("response_body", getResponseBody(response));

            // JSON 형식으로 응답 로그 기록
            logger.info(objectMapper.writeValueAsString(logMap));
        } catch (Exception e) {
            logger.error("Error logging response", e);
        }
    }

    private String getRequestBody(ContentCachingRequestWrapper request) {
        byte[] content = request.getContentAsByteArray();
        if (content.length > 0) {
            return new String(content);
        }
        return "[No Request Body]";
    }

    private String getResponseBody(ContentCachingResponseWrapper response) {
        byte[] content = response.getContentAsByteArray();
        if (content.length > 0) {
            return new String(content);
        }
        return "[No Response Body]";
    }
}

```

**2. `Bean` 등록**

- **필터**는 기본적으로 **스프링 컨텍스트에 빈(Bean)으로 등록**되면, **자동으로 필터 체인에 포함 됨**

- 이때 명시적 우선순위 설정 없이 필터를 등록하면, Spring Boot는 필터를 체인의 마지막 부분에 등록되므로, 해당 필터는 가장 앞쪽에 위치해야 되기 때문에 `FilterRegistrationBean<>` 과 `setOrder(Ordered.HIGHEST_PRECEDENCE)`를 사용해 가장 높은 우선 순위를 부여함
  -> 이렇게 안하면, Security 필터체인도 라이브러리 자체적으로 우선 순위가 높게 설정되어 있어 `LoggingFilter`가 `Security`필터체인 뒤쪽으로 감 

  ```java
  @Configuration  
  public class BeanConfig {
        @Bean
          public FilterRegistrationBean<LoggingFilter> loggingFilter() {
  
              FilterRegistrationBean<LoggingFilter> registrationBean = new FilterRegistrationBean<>();
              LoggingFilter loggingFilter = new LoggingFilter();
  
              registrationBean.setFilter(loggingFilter);
              registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
              registrationBean.addUrlPatterns("/*");
              
              return registrationBean;
      }
  }
  ```

**3. logback.xml에 로깅 클래스 설정**

- 콘솔에 출력되는 로그는 별도로 처리 하지 않았음 (어짜피 스프링이 알아서 해줌)
- `Local`환경은 `Prod` 환경 전에 파일에 잘 기록되는지 테스트하려고 설정 한 거임 
  -> 실제 디버깅 때는 그냥 콘솔 창 로그 사용 할 꺼니 필요 없음 
- 일반 로그 메시지는 설정한 `FILE_LOG_PATTERN`에 따라 기록 됨
  - 스택 트레이스는 패턴을 따르지 않음

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<configuration>
    <conversionRule conversionWord="clr" converterClass="org.springframework.boot.logging.logback.ColorConverter" />
    
    <property name="FILE_LOG_PATTERN" value="%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %5level %logger - %msg - %X{transactionId} %n" />
    
    <!-- 파일 로깅 설정 및 롤링 (모든 로그 수집, 100MB마다 파일 회전) -->
    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <encoder>
            <pattern>${FILE_LOG_PATTERN}</pattern>
        </encoder>
        <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
            <fileNamePattern>./log/%d{yyyy-MM-dd}.%i.log</fileNamePattern>
            <maxFileSize>100MB</maxFileSize>
            <maxHistory>30</maxHistory>
        </rollingPolicy>
    </appender>

    <!-- Local 환경에서는 DEBUG 로그 활성화 -->
    <springProfile name="local">
        <root level="debug">
            <appender-ref ref="FILE" />
        </root>
        
        <!-- LoggingFilter 로그 설정 -->
        <logger name="com.drive.sidepjt.common.filter.LoggingFilter" level="debug" additivity="false">
            <appender-ref ref="FILE" />
        </logger>
    </springProfile>

    <!-- Production 환경: 기본 INFO, 필요시 DEBUG/TRACE 동적 변경 가능 -->
    <springProfile name="prod">
        
        <!-- 운영 환경에서는 기본 INFO 이상 로깅 -->
        <root level="info">
            <appender-ref ref="FILE" />
        </root>
        
        <!-- LoggingFilter는 동적으로 로깅 레벨 변경 가능 -->
        <logger name="com.drive.sidepjt.common.filter.LoggingFilter" level="info" additivity="false">
            <appender-ref ref="FILE" />
        </logger>
    </springProfile>
</configuration>
```



### 4. 참고

- **CommonsRequestLoggingFilter**
  - Spring은 **`CommonsRequestLoggingFilter`**라는 구현체를 제공하여, **간단하게 요청 정보를 로그로 남길 수 있도록** 지원

  - 이 필터는 요청이 들어올 때 **URI**, **HTTP 메서드**, **쿼리 파라미터** 등 **요청에 관한 주요 정보를 자동으로 기록**해 줌

  - 요청 바디(payload)까지 로깅하도록 설정할 수 있음

