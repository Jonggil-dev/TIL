# Spring Security Request URL 처리 과정

1. **Spring Security는 모든 request에 대해 모든 filterChain을 지나도록 함** (다만, 해당 filter에서 해당 요청 URI에 대해 작업을 진행할 건지, 아니면 그냥 다음 filter로 넘길 것인지를 동작하게 하는거임) 
2. **`permitAll()`로 설정된 URL에 대해서는 FilterChain 내에서 인증 또는 인가 관련 예외(`AuthenticationException`, `AccessDeniedException`)가 발생하더라도 `ExceptionTranslationFilter`가 개입하지 않도록 하여,  예외가 발생하더라도 이를 정상적으로 처리하여 클라이언트에게 예외 응답 대신 원래의 요청 흐름을 유지하도록 함**
