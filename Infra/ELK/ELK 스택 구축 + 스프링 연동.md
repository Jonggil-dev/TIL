# ELK 스택 구축 + 스프링 연동

### 0. 참고

- [elastic stack 구축하여 로그 수집, 관리 하기 (+ spring boot)](https://dgjinsu.tistory.com/34)

---

### 1. 사용 방법

#### **(1) Spring Boot Application에서 로그를 수집**

-  logback 기능을 활용하여 console에 찍히는 로그들을 파일로 백업

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
          <logger name="com.feelcoding.logbackdemo" level="DEBUG" />
          <root level="INFO">
              <appender-ref ref="CONSOLE" />
              <appender-ref ref="FILE" />
          </root>
      </springProfile>
  <!--    <springProfile name="dev|stg">-->
  <!--        <root level="INFO">-->
  <!--            <appender-ref ref="CONSOLE" />-->
  <!--            <appender-ref ref="FILE" />-->
  <!--        </root>-->
  <!--    </springProfile>-->
      <springProfile name="prod">
          <root level="INFO">
              <appender-ref ref="CONSOLE" />
              <appender-ref ref="FILE" />
          </root>
      </springProfile>
  </configuration>
  ```



#### **(2) ELK 설정 및 실행 (Docker)**

- 참고 사이트 보기