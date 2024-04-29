# Redis Configuration

### 1. Redis-Client 

- **개요**
  - Redis 클라이언트는 Redis 데이터베이스와 상호작용하기 위한 도구. 이 클라이언트를 사용하여 Redis 서버에 명령을 보내고, 데이터를 저장하거나 검색할 수 있음
- **종류 (Lettuce vs Jedis)**
  - **Lettuce**: 자바에서 널리 사용되는 비동기, 이벤트 기반의 클라이언트로, 넌블로킹 I/O를 통해 높은 성능을 제공
  - **Jedis**: 또 다른 자바 기반 Redis 클라이언트로, 간단하고 동기적인 방식을 제공

- **사용 방법**

  - **spring-boot-starter-data-redis 종속성 추가 후 Redis-Client의 별도 설정을 하지 않는다면, Spring Boot가 자동으로 Lettuce를 사용함** 
    (Lettuce가 넌블로킹 I/O를 기반으로 비동기 처리가 필요한 많은 현대 애플리케이션에 더 적합하기 때문)

  - **Redis-Client 설정 및 빈 등록 방법**

    ```java
    @Configuration
    public class RedisConfig {
        @Value("${spring.data.redis.host}")
        private String host;
    
        @Value("${spring.data.redis.port}")
        private int port;
    
        @Bean
        public RedisConnectionFactory redisConnectionFactory() {
            RedisStandaloneConfiguration redisConfiguration = new RedisStandaloneConfiguration();
            redisConfiguration.setHostName(host);
            redisConfiguration.setPort(port);
            return new LettuceConnectionFactory(redisConfiguration);
        }
    }
    ```

  

### 2. Redis-Template 

- **개요**
  - Redis Template는 Redis와의 상호작용을 간단하게 만들어 주는 도구
  - Redis에서 데이터를 쉽게 읽고 쓸 수 있도록 객체 직렬화, 연결 관리 등 다양한 메소드를 제공

- **Redis-Template Serializer** (출처 : [serializer 설명](https://velog.io/@hkyo96/Spring-RedisTemplate-Serializer-%EC%84%A4%EC%A0%95))

  - Redis Template는 데이터를 직렬화, 역직렬화 하여 저장, 조회하므로 적절한 직렬화 방식을 설정해주어야 함.

  - RedisTemplate에 serializer를 별도로 설정하지 않으면, 직접 redis-cli로 데이터 확인이 어려움 (스프링을 통해서는 확인 가능)

  - **Serizlizer 구현체 종류**

    - **JdkSerializationRedisSerializer (Default)**

      - 디폴트로 적용되는 Serializer로, 기본 자바 직렬화 방식을 사용

      - 자바 직렬화는 `java.io.Serializable` 인터페이스만 구현하면 별도의 작업 없이 간편하게 사용 가능하다는 장점이 있지만, DB 등에 장기간 저장하는 정보에 사용하기에는 여러 단점들이 존재한다.

    - **GenericJackson2JsonRedisSerializer**
      -  별도의 Class Type을 지정할 필요 없이 자동으로 Object를 Json으로 직렬화해주는 것이 장점. 하지만 Object의 Class Type을 포함한 데이터를 저장하게 된다는 단점이 존재

    - **Jackson2JsonRedisSerializer**
      - @class 필드를 포함하지 않고 Json으로 저장. 
      - 하지만, 항상 ClassType을 Serializer에 함께 지정해주어야 함. 즉, RedisTemplate 객체를 저장하는 DTO 타입 별로 생성해서 각각 Serializer의 ClassType을 지정해주어야 한다는 것이다.

    - **StringRedisSerializer**
      - 이름처럼 String 값을 그대로 저장하는 Serializer
      - 객체를 Json형태로 변환하여 Redis에 저장하기 위해서는 직접 Encoding, Decoding을 해주어야 한다는 단점이 존재

- **사용 방법 **(출처 : [config 설정 ](https://developer-nyong.tistory.com/21))

  - **Spring boot 2.0부터 RedisTemplate, StringRedisTemplate가 자동 생성 되어 빈으로 등록 됨. 즉, 별도의 Serializer를 설정할 것이 아니라면, 따로 빈에 등록안해도 됨**

    - `StringRedisTemplate`은 `RedisTemplate`의 특수화된 형태로, Redis에서 key, value가 모두 문자열인 데이터를 처리할 때 사용

  - **Serializer 설정, 빈 등록 방법**

    - 해당 예시는 key - value 가 String - Object이고, 모두 StringRedisSerializer를 사용하는 redisTemplate 설정 코드임

    ```java
    package com.example.icecream.common.config;
    
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.data.redis.connection.RedisConnectionFactory;
    import org.springframework.data.redis.core.RedisTemplate;
    import org.springframework.data.redis.serializer.StringRedisSerializer;
    
    
    @Configuration
    public class RedisConfig {
    
        @Bean
        public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
            RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
            redisTemplate.setConnectionFactory(connectionFactory);
            redisTemplate.setKeySerializer(new StringRedisSerializer());
            redisTemplate.setValueSerializer(new StringRedisSerializer());
            redisTemplate.setHashKeySerializer(new StringRedisSerializer());
            redisTemplate.setHashValueSerializer(new StringRedisSerializer());
            return redisTemplate;
        }
    }
    
    ```