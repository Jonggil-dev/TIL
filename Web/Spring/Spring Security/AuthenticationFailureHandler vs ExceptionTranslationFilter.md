## AuthenticationFailureHandler vs ExceptionTranslationFilter

- 둘다 filterChain의 예외를 처리하는 곳이라는 공통점이 있음
- 기본적으로 흐름은 `Security FilterChain 예외 처리 방법 TIL`을 참고 하면 됨
- 주된 차이점
  - `AuthenticationFailureHandler`는 `AuthenticationFilter`에서 발생하는 인증/인가 예외를 직접 처리하는 Handler
  - `ExceptionTranslationFilter`는 Security의 마지막에 위치하기 때문에 필터 체인의 범용적인 인증/인가 예외를 처리하는 필터라고 생각하면 됨
- 왜 구별되어 있는건가? (내 추측)
  - 필터 체인 맨 마지막 단계에는 `FilterSecurityInterceptor`에서 최종적으로 사용자의 인증/인가 여부를 확인하는 로직이 수행 됨. 이 때 발생하게 되는 인증/인가 예외의 상황에 대해서만 `ExceptionTranslationFilter` 가 예외 처리를 함
  - `AuthenticationFilter` 같은 경우 `ExceptionTranslationFilter`보다 앞쪽에 위치하여 필터의 로직 중 발생한 예외를 throw 하여도 `ExceptionTranslationFilter`로 전달되지 않음 (throw는 상위 스택으로 던져지기 때문에, 즉 순서 상 앞쪽에 위치한 곳으로만 던져짐)
  - 그렇기 때문에 `ExceptionTranslationFilter`보다 앞쪽 필터에서 발생한 인증/인가 예외에 대한 부분은 `ExceptionTranslationFilter`가 처리할 수 없으며, 각 필터들은 예외를 본인 필터 내에서 직접 처리할 수 있어야 함
  - 그 중의 하나로`AuthenticationFilter`에서 발생하는 `AuthenticationException`을 직접 처리하기 위해 존재하는 것이 `AuthenticationFailureHandler` 임
    - 예를 들면 `AuthenticationFilter`의 `AuthenticationManager.authenticate()` 에서 발생하는 `AuthenticationException` 등을 처리