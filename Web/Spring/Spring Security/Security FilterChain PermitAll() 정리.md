# FilterChain PermitAll() 정리

- ### 참고자료

  - [SecurityConfig 클래스의 permitAll() 이 적용되지 않았던 이유](https://velog.io/@choidongkuen/Spring-Security-SecurityConfig-%ED%81%B4%EB%9E%98%EC%8A%A4%EC%9D%98-permitAll-%EC%9D%B4-%EC%A0%81%EC%9A%A9%EB%90%98%EC%A7%80-%EC%95%8A%EC%95%98%EB%8D%98-%EC%9D%B4%EC%9C%A0)
  - **TIL -> Spring Security FilterChain 예외 처리 방법 -> 2번이랑 같이 알아야 정확히 이해가 됨**

- ### PermitAll()이 적용된 url에 대한 request 처리 방식

  - **permitAll() 을 적용해도, 구성된 `Spring Security` 의 필터 체인은 모두 겨처가긴 함 (필터에 작성된 로직에 따라 그냥 다음 필터로 넘어가거나 로직이 수행 됨)**
  - 그렇게, 필터 체인을 모두 거치고 필터 체인의 마지막 필터인 `FilterSecurityInterceptor`에서 인증 및 인가를 처리 로직을 수행함. **이 때 `FilterSecurityInterceptor`에서 URI가 `permitAll() request `인지를 확인함** 
  - 이 때 `FilterSecurityInterceptor`에서는 적절하지 않은 인증/인가에 대해 `ExceptionTranslationFilter`로 예외를 던져 처리하는데, **`permitAll() request`의 경우 인증/인가 예외에 대해 `ExceptionTranslationFilter` 가 개입하지 않고 정상적으로 API 호출이 이루어지도록 동작하는거임**
  - 다른 말로, `SecurityContext` 에 `Authentication` 인증 객체가 존재하지 않더라도 `permitAll() request `라면 정상적으로 넘어 간다는 뜻
  - 위에서 말한  `FilterSecurityInterceptor`에서 던지는 인증/인가 예외는 `AuthenticationException` 과 `AccessDeniedException`만 해당함
  - **반대로 말하면 위의 동작 과정에서 어긋나는 로직이 있는경우는 `permitAll()`과 상관없이 클라이언트에게 에러가 반환됨**
    - 예시
      - 필터체인 내  `FilterSecurityInterceptor`가 아닌 곳에서 발생하는 인증/인가 에러 
        (해당 에러를 `dofilter()`로 catch하여 `FilterSecurityInterceptor`에서 처리 되도록 구성해 놓았다면 괜찮음)
      - 필터체인 내 `AuthenticationException`, `AccessDeniedException`가 아닌 예외를 명시적으로 발생시키는 경우 `throw new XXXException`을 사용한 경우
