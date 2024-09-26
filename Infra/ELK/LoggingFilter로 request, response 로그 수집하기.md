# LoggingFilter로 request, response 로그 수집하기

### 0. 참고

- 채택 안함
  - [AbstractRequestLoggingFilter를 이용한 요청, 응답 로깅 1 / 2](https://keichee.tistory.com/467)
  - [AbstractRequestLoggingFilter를 이용한 요청, 응답 로깅 2 / 2](https://keichee.tistory.com/468)

- 채택 방법 : GPT 참고

---

### 1. 개요

- 우선 request와 response 로그를 수집하려면 해당 로그를 출력을 해야됨
- Spring의 전역 로깅 설정은 서비스 내부의 비즈니스 로직 실행 중에 발생하는 로그를 기록함. **하지만 필터는 비즈니스 로직에 진입하기 전의 요청 상태나 응답 후의 최종 상태를 기록할 수 있기 때문에, 애플리케이션 레벨에서 잡히지 않는 정보를 수집하는 데 유리함.**
- 결국 위 2가지 이유로 request와 response를 로그를 수집하는 필터를 만들어서 필터체인에 등록해야 됨



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

```java
package com.drive.sidepjt.common.filter;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class LoggingFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(LoggingFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 인코딩 설정 (UTF-8)
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        ContentCachingRequestWrapper cachingRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper cachingResponse = new ContentCachingResponseWrapper(response);

        try {
            filterChain.doFilter(cachingRequest, cachingResponse);
        } finally {
            logRequestDetails(cachingRequest);
            logResponseDetails(cachingResponse);
            cachingResponse.copyBodyToResponse();
        }
    }

    private void logRequestDetails(ContentCachingRequestWrapper request) {
        String method = request.getMethod();
        String uri = request.getRequestURI();
        String queryString = request.getQueryString();
        String clientIP = request.getRemoteAddr();

        // 'Authentication' 헤더 가져오기
        String authHeader = request.getHeader("Authorization");  
        if (authHeader == null) {
            authHeader = "[No Authentication Header]";
        }

        // 요청 본문 가져오기
        String requestBody = getRequestBody(request);

        logger.info("Request: method={}, uri={}{} from IP={}", method, uri, queryString != null ? "?" + queryString : "", clientIP);
        logger.info("Request Authentication Header: {}", authHeader);  
        logger.info("Request Body: {}", requestBody);
    }

    private void logResponseDetails(ContentCachingResponseWrapper response) {
        int status = response.getStatus();
        String responseBody = getResponseBody(response);
        logger.info("Response: status={}", status);
        logger.info("Response Body: {}", responseBody);
    }

    private String getRequestBody(ContentCachingRequestWrapper request) {
        byte[] content = request.getContentAsByteArray();
        if (content.length > 0) {
            return new String(content); // 별도의 인코딩 지정 없이 기본 인코딩 사용
        }
        return "[No Request Body]";
    }

    private String getResponseBody(ContentCachingResponseWrapper response) {
        byte[] content = response.getContentAsByteArray();
        if (content.length > 0) {
            return new String(content); // 별도의 인코딩 지정 없이 기본 인코딩 사용
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

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<configuration>
    <conversionRule conversionWord="clr" converterClass="org.springframework.boot.logging.logback.ColorConverter" />

    <property name="CONSOLE_LOG_PATTERN" value="%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %clr(%5level) %cyan(%logger) - %msg%n" />
    <property name="FILE_LOG_PATTERN" value="%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %5level %logger - %msg%n" />

    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>${CONSOLE_LOG_PATTERN}</pattern>
        </encoder>
    </appender>

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

    <springProfile name="local">
        <root level="debug">
            <appender-ref ref="CONSOLE"/>
        </root>
        <logger name="root" level="error" additivity="false">
            <appender-ref ref="FILE" />
        </logger>
        <logger name="com.drive.sidepjt.common.filter.LoggingFilter" level="debug" additivity="false">
            <appender-ref ref="CONSOLE" />
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

