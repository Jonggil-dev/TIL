# FilterChain PermitAll() 정리

- ### 참고자료

  - [바로가기](https://velog.io/@choidongkuen/Spring-Security-SecurityConfig-%ED%81%B4%EB%9E%98%EC%8A%A4%EC%9D%98-permitAll-%EC%9D%B4-%EC%A0%81%EC%9A%A9%EB%90%98%EC%A7%80-%EC%95%8A%EC%95%98%EB%8D%98-%EC%9D%B4%EC%9C%A0)

- ### PermitAll()

  - `PermitAll()` 메서드는 `Security Config` 에서 인증 여부와 관계 없이 접근을 허용하는 속성임
  - 해당 URL 에 대한 모든 사용자의 요청을 허용하는 메소드

- ### PermitAll()이 적용된 url에 대한 request 처리 방식

  - **permitAll() 을 적용해도, 구성된 `Spring Security` 의 필터 체인은 모두 거침**

  - 다만, 모든 필터 체인을 거친 후 인증 정보가 없어도 즉, `SecurityContext` 에 `Authentication` 인증 객체가 존재하지 않거나, 필터 동작 과정 중 예외가 발생해도 해당 API 호출이 정상적으로 가능하게 하는 것이 PermitAll()의 동작 방식임

  - 위에서 말한 "필터 동작 과정 중 예외가 발생해도"라는 말은 오직 인증 및 인가 프로세스와 관련된 Spring Security 자체의 예외들에만 적용되는 말임. 즉,  명시적으로 발생시킨 예외(예: `new throw RuntimeException()`)는 `permitAll()` 설정의 영향을 받지 않음

    > **필터 동작 과정 중 예외가 발생하는 경우**:
    >
    > 1. **일반적인 상황**: Spring Security의 필터 체인에서 어떤 필터가 예외를 발생시키면, 이 예외는 보통 `ExceptionTranslationFilter`에 의해 캐치되고 적절히 처리됩니다. 예외의 종류에 따라 인증 실패 페이지로 리다이렉션되거나, 401(Unauthorized) 또는 403(Forbidden) 같은 상태 코드가 반환될 수 있습니다.
    > 2. **`permitAll()`이 적용된 경우**: `permitAll()`이 설정된 경로에 대해선, 필터 체인은 예외가 발생하더라도 그 예외를 무시하고 요청을 계속 처리합니다. 이는 `ExceptionTranslationFilter`가 활성화되지 않기 때문입니다. 즉, 인증 실패 등의 보안 예외가 발생하더라도 이를 무시하고 요청이 정상적으로 처리됩니다. 사용자는 인증되지 않은 상태로 API를 호출할 수 있습니다.

- ### 정리

  - **만약 모든 필터 체인을 거쳤는데 인증 객체를 담는 `SecurityContext` 에 인증 객체가 존재하지 않으면, 해당 요청이 인증되지 않았음을 의미함. 그러나, 만약 해당 API 에 permitAll()을 적용한다면 `SecurityContext` 에 인증 객체가 존재 여부와 상관 없이 API 호출이 이루어짐**
  - **정리하자면, permitAll() 적용 시, 필터 체인 동작 과정에서 Spring Security 자체 적인 인증/인가 예외가 발생해도 ExceptionTranslationFilter 을 거치지 않으며, 인증 객체 존재 여부 상관 없이 정상적으로 API 호출이 이루어짐.**

