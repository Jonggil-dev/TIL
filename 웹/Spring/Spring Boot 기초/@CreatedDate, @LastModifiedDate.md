# @CreatedDate, @LastModifiedDate

- ### 출처 : https://ppusda.tistory.com/79

- ### 개요

  - created_at, updated_at 필드에 자동으로 날짜 생성하게 해서 DB에 저장하게 해주는 어노테이션
  - **보통 사용법은 BaseEntity에 created_at, updated_at를 만들어 두고 해당 필드가 필요한 Entity는 BaseEntity를 상속받아서 자동으로 적용되게 함**

- ### 사용법

  - **단순히 사용할 필드에 @CreatedDate, @LastModifiedDate 두 어노테이션을 붙히기만 하는 것이 아닌 아래 두 어노테이션을 통해 기능을 활성화 해야지 정상적으로 동작한다.**
    - **`@EnableJpaAuditing`** 
      - **용도**: Spring Data JPA의 감사(auditing) 기능을 활성화시키는 어노테이션. 이를 통해 엔티티의 생성 시간, 수정 시간, 생성자, 수정자 등을 자동으로 관리할 수 있게 해줍니다.
      - **적용 예**: `@CreatedDate`, `@LastModifiedDate`, `@CreatedBy`, `@LastModifiedBy` 등의 어노테이션과 함께 사용
      - **설정 위치**: 일반적으로 Spring Boot 애플리케이션의 메인 클래스나 @Configuration이 붙은 설정 클래스 위에 위치
    - **`@EntityListeners`**
      - **용도**: 특정 엔티티에 대한 이벤트 리스너를 지정하는 어노테이션입. 이는 엔티티의 생명주기(생성, 수정, 삭제 등)에 따른 특정 동작을 수행하도록 할 때 사용됩니다.
      - **적용 예**: 엔티티 클래스에 @EntityListeners를 사용하고, 리스너 클래스를 지정하여 해당 엔티티에 대한 생성, 수정, 삭제 등의 이벤트에 대한 커스텀 로직을 실행할 수 있습니다.
      - **리스너 클래스 예**: `AuditingEntityListener.class`가 JPA의 @CreatedDate 등과 함께 사용되어 엔티티의 생성/수정 시간을 자동으로 채우는 역할을 합니다.

  - **참고**
    - **`@CreationTimestamp`**
      - **용도**: Hibernate에서 제공하는 어노테이션으로, 엔티티의 특정 필드가 데이터베이스에 처음 저장될 때 현재 시간을 자동으로 할당
      - **이 어노테이션은 Hibernate 레벨에서 동작하므로, Spring Data JPA의 감사 기능인 `@EnableJpaAuditing`과는 독립적으로 작동합니다. 따라서 `@EnableJpaAuditing`을 활성화하지 않아도 `@CreationTimestamp`은 영향을 받지 않고 정상적으로 작동합니다.**
    - `@MappedSuperclass` :  JPA (Java Persistence API)에서 사용되며, 특정 클래스가 엔티티 클래스의 슈퍼클래스로 사용되지만, 그 자체로는 데이터베이스 테이블과 직접적으로 매핑되지 않는 경우에 사용

  - **예시**

  ```java
      @SpringBootApplication
      @EnableJpaAuditing
      public class TestApplication {
  
          public static void main(String[] args) {
              SpringApplication.run(TestApplication.class, args);
          }
      }
  ```

  ```java
  @Getter
  @MappedSuperclass
  @EntityListeners(AuditingEntityListener.class)
  public class BaseEntity {
  
      @CreatedDate
      @Column(updatable = false)
      private LocalDateTime createDate;
  
      @LastModifiedDate
      private LocalDateTime modifiedDate;
  }
  ```

  ```java
  @Entity 
  @Getter
  @NoArgsConstructor
  public class Question extends BaseEntity{
  
      @Id
      @GeneratedValue
      private Long id;
  
          @Column(length = 100)
      private String subject;
  
      @Column(columnDefinition = "TEXT")
      private String content;
  
  }
  ```