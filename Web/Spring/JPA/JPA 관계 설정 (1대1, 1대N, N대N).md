# JPA 관계 설정 (1 : 1, 1 : N, N : N)

### 1. `1 : 1` 관계 (`@OneToOne`)

- https://innovation123.tistory.com/178 참고



### 2. `1 : N` 관계 (`@ManyToOne, @OneToMany`)

#### (1) `1 : N` 관계에서 연관 관계의 주인

- https://ksh-coding.tistory.com/112

- **DB 테이블에서 FK 필드를 가지고 있는 쪽이 연관 관계의 주인이라고 표현함**

1. **DB 테이블 관계에서의 FK 관리 (N이 연관관계의 주인)**
   - 일반적으로 관계형 데이터베이스 테이블에서는 1:N 관계를 설정할 때, **항상 N 쪽의 테이블이 FK를 관리하도록만 설계가 가능함** 
   - 예를 들어, `User` : `Post = 1 : N` 관계에서 `Post` 테이블이 `Member`의 FK를 포함하게 됨
   - 그 이유는 FK를 관리하는 2가지 각 상황에서, 1쪽의 레코드의 Update가 발생했을 때를 생각해보면 바로 이해됨 
     (1쪽이 연관관계의 주인이 된다면 굉장히 비효율적인 작업이 되버림)
2. **엔티티에서의 FK 관리**
   - JPA와 같은 ORM에서는 1:N 관계를 표현할 때, 양쪽(1 쪽과 N 쪽) 모두 FK를 관리할 수 있는 유연성을 제공함 **(실제 DB 테이블에는 `@ManyToOne`을 사용한 엔티티, 즉 N 쪽의 테이블에만 FK가 생김)**
   -  엔티티 측면에서는 "`@OneToMany` 어노테이션을 통해 1 쪽에서도 N 쪽의 레코드를 참조할 수 있도록 한다는 것"이 양방향에서 FK를 관리할 수 있다라는 의미임

#### (2) `@ManyToOne` 과 `@OneToMany`

- 1: N 관계를 매핑해야 할 때 사용

- N 쪽에 `@ManyToOne`,1쪽에 `@OneToMany`를 사용함

  - `@ManyToOne`만 단독으로 사용하면 N에서 1을 참조하는게 정상적으로 가능함 (1에서 N 참조 불가능)

  - `@OneToMany`만 단독으로 사용하면 1에서 N을 참조하는게 불가능 (의미가 없음). 즉, 1에서도 N을 참조하고 싶으면 `@ManyToOne`과 함께 사용해야함

    - **`@ManyToOne`**

      ```java
      @Entity
      public class Post {
          @Id
          @GeneratedValue(strategy = GenerationType.IDENTITY)
          private Long id;
      
          @ManyToOne
          @JoinColumn(name = "user_id") // 외래 키로 사용할 컬럼 이름 지정
          private User user; // User 엔티티 참조
      }
      ```

    - **@OneToMany**

      ```java
      @Entity
      public class User {
          @Id
          @GeneratedValue(strategy = GenerationType.IDENTITY)
          private Long id;
      
          @OneToMany(mappedBy = "user") // Post 엔티티 내의 User를 참조하는 필드 이름
          private List<Post> posts;
      }
      ```

  

### 3. `N : N` 관계 (`@ManyToMany`)

- https://codeung.tistory.com/254 참고