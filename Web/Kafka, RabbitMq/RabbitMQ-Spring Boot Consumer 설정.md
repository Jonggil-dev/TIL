# RabbitMQ-Spring Boot Consumer 설정

### 1. 의존성 추가
```groovy
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-amqp'
}
```

### 2. application.properties 설정

```properties
spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
spring.rabbitmq.username=guest
spring.rabbitmq.password=guest
```

- 참고

  - properties를 명시하지 않으면, `org.springframework.boot:spring-boot-starter-amqp` 의존성에 기본으로 설정되어 있는 아래 설정으로 RabbitMq가 연결을 시도함 (즉, 기본 설정과 RabbitMq 세팅이 같을 경우에는 별도 properties를 작성하지 않더라도 연결이 됨)

  - 의존성에 내포된 기본 설정 값

    ```properties
    spring.rabbitmq.host=localhost
    spring.rabbitmq.port=5672
    spring.rabbitmq.username=guest
    spring.rabbitmq.password=guest
    ```

### 3. RabbitMQ 설정 활성화

Spring에서는 `@RabbitListener` 애노테이션을 사용하여 특정 큐를 구독하는 메소드를 간단히 정의할 수 있습니다. 먼저, `RabbitListener`가 동작하도록 필요한 설정을 Java Config 파일에 추가해야 합니다:

```java
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class RabbitConfig {
}
```

- `@Configuration` :  클래스가 스프링의 DI 컨테이너에 의해 빈(Bean) 설정을 제공하는 클래스임을 나타내는 어노테이션. 이러한 설정 클래스 내부에서 `@Bean` 어노테이션을 사용해 스프링 컨테이너가 관리할 객체(빈)를 직접 선언하고 초기화함.

- `@EnableRabbit`

  > **`@EnableRabbit`은 `org.springframework.boot:spring-boot-starter-amqp` 에 의해 기본적으로 활성화 되기 때문에 명시적으로 작성하지 않더라도 `@EnableRabbit`어노테이션이 실행 됨**

  - `@EnableRabbit`은 사실상 메타-어노테이션이며, 이것이 선언된 설정 클래스에 대한 구성을 활성화하는 트리거 역할을 합니다. Spring Boot 애플리케이션에서 `@EnableRabbit`을 사용하면, Spring은 이 어노테이션과 연관된 설정(또는 구성 클래스)을 로드하고 실행합니다.

  - `@EnableRabbit` 어노테이션의 내부 동작 과정
    - `@EnableRabbit` 은 `@Import(RabbitBootstrapConfiguration.class)`를 내부적으로 포함하고 있음. `@Import`는 지정된 구성 클래스를 현재 스프링 애플리케이션 컨텍스트에 추가할 때 사용
    - `RabbitBootstrapConfiguration`는 RabbitMQ 관련 빈들을 스프링 컨텍스트에 등록하는 설정들을 포함.
    - `@EnableRabbit`을 통해 활성화된 설정은 `RabbitListenerAnnotationBeanPostProcessor`라는 빈을 등록. 이 빈은 스프링 컨테이너가 초기화되는 과정에서 모든 빈을 검사하여 `@RabbitListener` 어노테이션이 붙은 메소드를 찾습니다.
    - 찾은 `@RabbitListener` 메소드에 대해, 메시지 리스너 컨테이너를 생성하고 구성합니다. 이 컨테이너는 RabbitMQ로부터 메시지를 수신하고, 해당 메소드를 콜백으로 호출하여 메시지를 처리하게 됩니다.

- **RabbitConfig 클래스 자체의 역할**

  - `RabbitConfig` 클래스는 사실상 `@EnableRabbit` 어노테이션을 적용하기 위한 "껍데기" 또는 "편의성" 클래스로 사용되는 것 뿐임. 이 클래스 자체는 `@EnableRabbit`을 포함함으로써 `RabbitBootstrapConfiguration`을 불러오고, 이로 인해 필요한 RabbitMQ 관련 빈들이 스프링 컨테이너에 자동으로 등록되게 하는 역할을 수행함
  - `RabbitConfig`와 `RabbitBootstrapConfiguration`가 어떠한 계층관계를 이루는게 아니라 메모리나 빈 측면에서도 그냥 2개가 독립적인 클래스임. 말그대로 `RabbitConfig`는 그냥 `RabbitBootstrapConfiguration`을 부르기 위한 셔틀같은 역할임.

### 4. 리스너 구현

```java
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class MessageListener {

    @RabbitListener(queues = "queue-name")
    public void receiveMessage(String message) {
        System.out.println("Received message: " + message);
    }
}
```
- **Message를 직접 String으로 수신해도 되지만, Dto를 활용하는 방법 추천**

  - 예시

    - Listener

    ```java
    @RequiredArgsConstructor
    @Component
    public class GPSMessageListener {
    
        private final CrosswalkService crosswalkService;
    
        @RabbitListener(queues = "crosswalk")
        public void receiveMessage(GPSMessageDto gpsMessageDto) {
            crosswalkService.checkCrosswalkArea(gpsMessageDto.getUserId(), gpsMessageDto.getLatitude(), gpsMessageDto.getLongitude());
        }
    }
    ```

    - Dto

    ```java
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    @NoArgsConstructor
    @Getter
    @ToString
    public class GPSMessageDto {
    
        private int userId;
        private int destinationId;
        private double latitude;
        private double longitude;
        private String timestamp;
    }
    ```

### 5. 메시지 처리 로직 구현

위의 `receiveMessage` 메소드 내부에서, 받은 메시지를 기반으로 필요한 비즈니스 로직을 구현.
