# Spring 컨테이너 초기화 및 Bean 등록 과정

1. **애플리케이션 실행 시작**
   - 애플리케이션이 시작되면 `ApplicationContext`가 생성됩니다. 이 컨텍스트는 애플리케이션의 설정 정보를 로드하고 빈 생명 주기를 관리합니다.

2. **컴포넌트 스캔 (Component Scanning)**
   - `@ComponentScan` 어노테이션 또는 Spring Boot의 자동 설정 기능을 통해, 지정된 패키지 내의 클래스를 스캔합니다.
   - `@Component`, `@Service`, `@Repository`, `@Controller` 등의 어노테이션이 붙은 클래스를 찾아 빈 등록 후보로 식별합니다.

3. **빈 정의 생성 (Bean Definition Creation)**
   - 스캔된 클래스에 대해 Spring은 "빈 정의"(`BeanDefinition`)를 생성합니다. 이 빈 정의는 클래스에 대한 메타데이터와 인스턴스 생성 및 관리 정보를 포함하고, Spring의 빈 팩토리에 등록됩니다.

4. **빈 생성 및 의존성 주입 (Bean Creation and Dependency Injection)**
   - `@Configuration` 클래스가 발견되면, 해당 클래스의 인스턴스가 생성되며, `@RequiredArgsConstructor`가 있다면 의존성이 주입됩니다.
   - 빈 팩토리는 빈 정의에 따라 의존성을 주입할 준비를 하고, 실제 클래스의 인스턴스를 생성합니다. 생성자 주입, 세터 주입, 필드 주입을 통해 의존성을 주입합니다.

5. **`@Bean` 메소드 호출**
   - `@Configuration` 클래스의 인스턴스가 생성된 후, Spring은 이 클래스 내의 `@Bean` 애너테이션이 붙은 메소드를 호출합니다. 각 메소드의 로직이 실행되고 반환된 객체가 빈으로 등록됩니다.

6. **초기화 및 사용 준비 (Initialization)**
   - 인스턴스 생성 후, 빈 후처리기(`BeanPostProcessor`)를 사용하여 추가 처리를 수행합니다. 예를 들어, `@PostConstruct` 어노테이션이 붙은 메소드를 실행합니다.
   - 빈 등록이 완료되면, 해당 인스턴스는 Spring 컨테이너에 완전히 등록되어 애플리케이션 전반에서 사용할 준비가 됩니다.
