# Spring 자기 참조 Entity 설계

- 상황 설명

  - User 테이블에는 유저 정보가 기록됨. 이 때 유저는 부모유저와 자녀유저가 있음
  - 그런데 자녀 유저의 경우 어떤 부모의 자녀인지를 확인할 수 있도록, 부모와 자녀의 연결 관계를 만들고 싶음
  - 부모, 자녀 유저가 각 테이블로 분리되어 있는게 아니기 때문에, 하나의 User 테이블을 참조하는 별도의 Mapping 테이블을 생성해야 됨
  - 결국은 User 테이블을 자기 참조하는 Mapping 테이블을 만들어서 해결

- 매핑 테이블 코드(Entity) 예시

  ```java
  package com.example.icecream.domain.user.entity;
  
  import jakarta.persistence.*;
  import jakarta.validation.constraints.NotNull;
  import lombok.*;
  
  @Entity
  @Table(name = "parent_child_mapping")
  @Getter
  @NoArgsConstructor(access = AccessLevel.PROTECTED)
  @AllArgsConstructor
  @Builder
  public class ParentChildMapping {
  
      @Id
      @GeneratedValue(strategy = GenerationType.IDENTITY)
      private int id;
  
      @ManyToOne(fetch = FetchType.LAZY)
      @JoinColumn(name = "parent_id", nullable = false)
      @NotNull
      private User parent;
  
      @ManyToOne(fetch = FetchType.LAZY)
      @JoinColumn(name = "child_id", nullable = false)
      @NotNull
      private User child;
  }
  
  ```

  - 코드 해석

    -  `ParentChildMapping `테이블 내에 `User`엔티티 의 인스턴스를 저장하는 `parent`와 `child` 필드가 있음

      - `@ManyToOne`의 역할

        - `@ManyToOne`은 "많은(many)" 쪽에서 "하나(one)" 쪽으로의 관계를 나타 냄
          **(Many는 작성된 Entity 객체 One은 field에 명시된 객체)**

        -  **쉽게, `ParetChildMapping `테이블안에서 레코드별로 비교해 봤을 때  `parent`랑 `child`가 동일한게 있을 수 있다는 뜻**

        - **예시**

          - `ParentChildMapping` 테이블은 다음과 같은 레코드를 포함할 수 있습니다:

            - `ParentChildMapping` 레코드 1: parent_id = 1, child_id = 2
            - `ParentChildMapping` 레코드 2: parent_id = 1, child_id = 3
            - `ParentChildMapping` 레코드 3: parent_id = 1, child_id = 4

            위의 예에서 `parent_id = 1`인 `User`는 여러 `ParentChildMapping` 인스턴스의 `parent` 필드와 연결되어 있음. 이는 해당 `User`가 여러 자녀를 가지고 있음을 나타냅니다. 따라서, 여러 개의 `ParentChildMapping` 레코드가 하나의 `User` (부모)를 참조하고 있기 때문에 "다대일(many-to-one)" 관계가 형성된다는 의미임

      - `@JoingColumn`의 역할

        - `ParentChildMapping`내의 `parent`와 `child` 필드는 `@JoinColumn`을 통해 각 `User` 객체가 가지고 있는 `id` 필드를 `ParentChildMapping` 테이블 내에서 `parent_id` 와 `child_id`  필드에 외래키로 지정함
        - 정리하면,  엔티티 클래스 내에서 `private User parent;` 필드는 객체의 참조를 저장하지만, `@JoinColumn(name = "parent_id")`을 통해 `parent` 필드가 데이터베이스에서 어떻게 표현되는지, 특히 `parent 인스턴스` 내의 어떤 컬럼과 연결되는지를 정의 함.

        > 이 때 `@JoinColumn`에서 `name` 속성으로 `parent_id`를 지정했다고 하더라도, 실제 `parent` 필드가 `User` 테이블의 어떤 컬럼을 참조하는지는 JPA가 자동으로 처리하는 부분 임
        >
        > ### 참조 컬럼의 결정 방법
        >
        > 1. **기본키 추정**: JPA는 엔티티 간의 관계를 설정할 때, 관계의 대상이 되는 엔티티 (`User` 엔티티)의 기본키를 기본적으로 참조합니다. 즉, `User` 엔티티에 `@Id` 애너테이션이 붙어있는 필드가 그 엔티티의 기본키로 간주되고, 이 기본키는 다른 테이블의 외래키가 자동으로 참조하게 됩니다.
        > 2. **애너테이션 지정**: 만약 `User` 엔티티가 다중 키를 사용하는 복합 키를 기본키로 사용하거나, 특정한 기본키를 참조하도록 설정해야 할 경우, `@JoinColumn` 애너테이션에 `referencedColumnName` 속성을 사용하여 명시적으로 참조할 컬럼을 지정할 수 있습니다. 예를 들어, `@JoinColumn(name = "parent_id", referencedColumnName = "id")`와 같이 지정할 수 있습니다.

