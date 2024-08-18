# MongoDB JPA 유니크 인덱스 생성 방법

1. properties에 spring.data.uto-index-creation = true 설정 추가

2. Document에 유니크 인덱스 설정 관련 코드 추가

   -  단일 유니크 인덱스의 경우

     - 필드에 `@Indexed(unique = true)` 추가

   - 복합 유니크 인덱스의 경우

     - 클래스에 `@CompoundIndex(def = "{ }", unique = true)` 추가

     ```java
     @Document(collection = "member_lists")
     @NoArgsConstructor(access = AccessLevel.PROTECTED)
     @AllArgsConstructor
     @Builder
     @Getter
     @CompoundIndex(def = "{ 'time': 1, 'car_number': 1 }", unique = true)
     public class MemberList {
     
         @MongoId
         private String id;
     
         private String time;
     
         @Field(name = "car_number")
         private String carNumber;
     
     }
     ```