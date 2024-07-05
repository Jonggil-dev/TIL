# Redis 데이터 타입 (Template, Generic, Serializer, OpsFor)

### 1. Redis 데이터 타입

- 레디스에서 데이터를 저장하는 방식은 기본적으로 키(Key)-값(Value) 구조를 가짐
- 값(Value)은 다양한 데이터 타입을 가질 수 있으며, 주요한 타입으로는 String, List, Set, Hash, Sorted Set 등이 있음

### 2. RedisTemplate 제네릭 타입

- 제네릭 타입은 `RedisTemplate`가 어떤 형태의 데이터만을 취급할 지 결정
- 예를 들어, `RedisTemplate<String, String>`은 레디스의 키(Key)와 값(Value) 모두 문자열로 처리하는 경우에 사용
  - `RedisTemplate<String, String>`에서 값(Value)이 문자열로 처리된다는 말은, `RedisTemplate`을 통해 값(Value)을 CRUD하는 메서드를 사용할 때, 처리할 때 `RedisTemplate`을 통해 값(Value)을 CRUD하는 메서드를 사용할 때, `Redis0Template`은 문자열 타입 데이터만 연산을 처리한다라는 의미임

### 3. Redis Serializer 설정

- `RedisTemplate`에 설정된 시리얼라이저(Serializer)는 `RedisTemplate`이 데이터를 저장/조회 할 때 등 데이터를 직렬화/역직렬화하는 역할 뿐임
- 결국, `RedisTemplate`은 제네릭 타입에 설정된 데이터 타입만을 취급하므로 Serializer는 해당 타입의 데이터를 직렬화/역직렬화 해서 저장/조회 하는 역할만 하는 거임 
- Serializer가 특별한 작업을 수행하는게 아니고 그냥 데이터 타입을 파악하고 해당 데이터 타입을 설정된 시리얼라이저의 직렬화 타입에 맞게 직렬화/역직렬화 하는게 다임

### 4. OpsFor~

- Redis의 데이터 구조별로 특화된 작업을 수행할 수 있도록 해주는 RedisTemplate Method
- **즉, OpsFor을 어떤걸 사용하느냐에 따라서 Redis에서 제공하는 자료구조(Value(문자열), List, Set, Hash 등)로 데이터가 저장되는 거임**
- 예시
  - OpsForValue를 사용하면, Redis에 데이터가 저장될 때 Redis에서 제공하는 Value(문자열) 자료구조 타입으로 데이터가 저장이 됨
    - OpsForValue를 사용해서 데이터를 저장할 때  RestTemplate의 value 제네릭이 List<Intger> 라면, 자바의 List<Intger> 데이터 타입이 설정된 시리얼 라이저에 따라 직렬화되고, 해당 직렬화된 값이 Redis value에 그냥 문자열 타입으로 저장 됨
  -  OpsForList를 사용하면, Redis에 데이터가 저장될 때 Redis에서 제공하는 List 자료구조 타입으로 데이터가 저장이 됨
    - OpsForList를 사용해서 데이터를 저장할 때  RestTemplate의 value 제네릭이 List<Intger> 라면, 자바의 List<Intger> 데이터 타입이 설정된 시리얼 라이저에 따라 직렬화되고, Redis value가 List 타입이고 해당 직렬화된 값이 각 리스트의 원소로 저장 됨 -> **즉, List안에 각 요소가 LIst로 저장 됨**
    - OpsForList를 사용해서 데이터를 저장할 때  RestTemplate의 value 제네릭이 Intger 라면, 자바의 Integer 데이터 타입이 설정된 시리얼 라이저에 따라 직렬화되고, Redis value가 List 타입이고 해당 직렬화된 값이 각 리스트의 원소로 저장 됨 -> **즉,  List안에 각 요소가 Int 타입으로 저장 됨**



### 5. 예시 (데이터 저장/조회 과정)

- Redis에서 제공하는 List 자료구조를 사용하며 각 원소가 Int 타입인 Redis 저장/조회 코드 예시

  - Redis Config

    ```java
    package com.example.icecream.common.config;
    
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.data.redis.connection.RedisConnectionFactory;
    import org.springframework.data.redis.core.RedisTemplate;
    import org.springframework.data.redis.serializer.GenericToStringSerializer;
    import org.springframework.data.redis.serializer.StringRedisSerializer;
    
    @Configuration
    public class RedisConfig {
    
        @Bean
        public RedisTemplate<String, Integer> redisTemplate(RedisConnectionFactory connectionFactory) {
            RedisTemplate<String, Integer> template = new RedisTemplate<>();
            template.setConnectionFactory(connectionFactory);
    
            template.setKeySerializer(new StringRedisSerializer());
            template.setValueSerializer(new GenericToStringSerializer<>(Integer.class));
    
            return template;
        }
    }
    ```

  - 데이터 저장/ 조회 코드 예시

    ```java
    package com.example.icecream.domain.map.service;
    
    import com.example.icecream.domain.map.entity.Crosswalk;
    import com.example.icecream.domain.map.repository.CrosswalkRepository;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.data.redis.core.ListOperations;
    import org.springframework.data.redis.core.RedisTemplate;
    import org.springframework.stereotype.Service;
    
    import org.locationtech.jts.geom.GeometryFactory;
    import org.locationtech.jts.geom.Coordinate;
    import org.locationtech.jts.geom.Point;
    
    @Service
    public class CrosswalkService {
    
        private final CrosswalkRepository crosswalkRepository;
        private final RedisTemplate<String, Integer> redisTemplate;
        private final GeometryFactory geometryFactory = new GeometryFactory();
    
        @Autowired
        public CrosswalkService(CrosswalkRepository crosswalkRepository, RedisTemplate<String, Integer> redisTemplate) {
            this.crosswalkRepository = crosswalkRepository;
            this.redisTemplate = redisTemplate;
        }
    
        public void checkCrosswalkArea(int userId, double latitude, double longitude) {
            Point point = geometryFactory.createPoint(new Coordinate(longitude, latitude));
            for (Crosswalk crosswalk : crosswalkRepository.findAll()) {
                String key = crosswalk.getCrosswalkName();
                ListOperations<String, Integer> listOperations = redisTemplate.opsForList();
                Long idx = listOperations.indexOf(key, userId);
    
                if (crosswalk.getCrosswalkArea().contains(point)) {
                    if (idx == null) {
                        listOperations.rightPush(key, userId);
                    }
                } else {
                    if (idx != null) {
                        listOperations.remove(key, 0, userId);
                    }
                }
            }
        }
    }
    ```

    



