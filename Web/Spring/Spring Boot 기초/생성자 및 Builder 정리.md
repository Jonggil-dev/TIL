# 생성자 및 Builder 정리

### 참고

- https://stir.tistory.com/52 (기본 생성자)
- https://jeongkyun-it.tistory.com/225#google_vignette (리플렉션)

---

### 1. 기본 생성자

- 스프링에서 **별도의 생성자를 명시하지 않으면 기본 생성자를 자동으로 생성**. 별도의 생성자가 1개라도 있는경우 기본 생성자 자동 생성 안됨 (필요한 경우 명시 해야 됨 -> `@NoArgsConstructor` 이용)

- **`Jackson`이나` JPA Entity` 사용 시 기본 생성자를 '무조건' 만들어줘야 함**

  - Jackson을 예로 들자면 `@RequestBody` 의 경우 DTO에 값을 세팅 하기 위해(Setter가 아니라) delegate 방식을 사용 하기 때문. 즉, 기본 생성자 안에서 매개변수가 있는 생성자를 호출 하는 방식으로 로직을 실행하는 방식을 취함

    > **delegate : 생성자 안에서 다른 생성자 호출하는 것**

  - JPA의 경우 DB에서 조회한 값을 Entity에 세팅할 때 Reflection이 사용되는데 해당 Reflcetion 과정은 기본 생성자가 요구되도록 설계 됨 **(reflection 기법 자체에 기본 생성자만 사용되는 건 아님(다른 생성자도 가능함))**

    - 예시로, JPA를 사용해 데이터베이스에서 조회한 데이터를 엔티티의 필드에 매핑 할 때를 생각해보면 
      `Entity entity = Repository.findById(3)` 이런식으로 만 코드를 작성해도 할당이 됨. 그런데 생각해보면 Entity 별로 가지고 있는 필드나 setter 메서드가 다른데 어떻게 자바가 알아차리고 해당 필드나 메서드에 접근하는지를 생각해보면 됨. **이 때 reflection을 이용해서 Entity의 내부 정보를 긁어온 다음 거기에 있는 필드나 메서드를 사용하는 거임**
    - **위의 reflection으로 Entity의 내부 정보를 긁어오기 위한 과정을 위해, 클래스 타입에 대한 정보가 필요한데 이 때 기본 생성자가 필요한 듯**
    
    > **Reflection** 
    >
    > - 구체적인 클래스 내부 정보를 알지 못하더라도 그 클래스의 메서드, 타입, 변수들에 접근할 수 있도록 해주는 자바 API
    > - 컴파일 시간이 아닌 실행 시간에 동적으로 특정 클래스의 정보를 추출할 수 있는 프로그래밍 기법
    > - **https://ebabby.tistory.com/4 이거 읽어보면 이해 잘됨**

### 2. `@NoArgsConstructor`

- 기본 생성자를 생성해주는 어노테이션
- AccessLevel.PROTECTED 속성과 주로 함께 사용
  - 해당 생성자를 protected 접근 제어자를 갖도록 하는 기능임
  - 무분별하게 객체를 만드는 것을 한 번 더 체크하여 방지하기 위함




### 3. `@RequiredArgsConstructor`

- 클래스의 `final` 필드나 `@NonNull`로 선언된 필드에 대해 생성자를 자동으로 생성해주는 어노테이션
- **의존성 주입(DI)과 연계하여 주로 사용하기도 함**
  - Spring에서는 **클래스의 생성자가 1개 밖에 없으면** 해당 생성자에 대해 `@Autowired` 어노테이션을 사용하지 않아도 생성자 주입이 적용됨
  - 그래서 의존성 주입이 필요한 필드에 대해 `final` 필드로 선언하고 클래스에 `@RequiredArgsConstructor`만 적용 해놓는다면 해당 필드들은 자동으로 의존성 주입이 적용됨



### 4. `@AllArgsConstructor`

- 클래스의 모든 필드를 매개변수로 받는 생성자를 자동으로 생성해주는 애노테이션



### 5. `@Builder`

- 객체 생성 패턴 중 하나로, 복잡한 객체를 단계별로 생성할 수 있게 해주는 디자인 패턴

- **`@NoArgsConstructor`와 단독으로 같이 사용하면 컴파일 에러가 발생함**

  (https://resilient-923.tistory.com/418)

  - `@Builder`의 동작을 위해서는 기본적으로 모든 필드를 파라미터로 사용하는 전체 생성자가 필요함
  - **`@~ArgsConstructor` 를 사용하지 않은 경우에만 모든 필드를 파라미터로 사용하는 생성자가 자동 생성 됨**
  - 그래서 `@Builder`와 `@~ArgsConstructor` 를 사용하는 경우 `@AllArgsConstructor`를 명시적으로 함께 사용하여야 컴파일 에러가 안남