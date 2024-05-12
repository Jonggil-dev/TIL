# OncePerRequestFilter vs GenericFilterBean

`GenericFilterBean`과 `OncePerRequestFilter`는 스프링 프레임워크의 서블릿 필터를 쉽게 구현할 수 있게 도와주는 추상 클래스

### 1. GenericFilterBean
- 스프링의 `Filter` 인터페이스를 구현하는 추상 클래스로, 필터 구현에 스프링의 환경 설정과 통합을 제공. 
- 이 클래스는 `BeanNameAware`, `BeanFactoryAware`, `InitializingBean`, 그리고 `DisposableBean` 인터페이스를 구현하여, 스프링 라이프사이클에 통합될 수 있도록 합니다.
- 이 클래스를 상속받은 필터는 스프링 빈으로서 관리될 수 있으며, 스프링의 의존성 주입, 프로퍼티 설정 등의 이점을 활용할 수 있습니다. 또한, 초기화와 종료 시점에 특정 로직을 실행할 수 있는 메서드(`initFilterBean`과 `destroy`)를 제공하여, 필터의 설정과 정리 작업을 용이하게 합니다.
- 보통 스프링 어플리케이션에서 필터가 스프링의 빈 설정과 밀접하게 연동되어야 할 때 사용

### 2. OncePerRequestFilter
- 스프링 특화 추상 클래스로, **각 요청마다 단 한 번씩만 필터 로직이 실행되도록 보장**
- **일반적인 서블릿 필터는 요청이 여러 필터나 서블릿을 통과할 때 여러 번 실행될 수 있는데, 이 클래스는 그러한 중복 실행을 방지함**
-  `doFilterInternal` 메서드를 제공하며, 실제 필터 로직은 이 메서드에 구현됨. `OncePerRequestFilter`는 요청이 이미 필터링되었는지를 추적하고, 중복 실행을 방지하기 위해 내부적으로 요청 속성을 확인
