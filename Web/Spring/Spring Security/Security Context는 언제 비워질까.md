# Security Context는 언제 비워질까

### 0. 참고

- https://itvillage.tistory.com/60

### 1. 정리

- 기본적으로 Security Context는 세션이 유효한 동안에 살아있음

  - 하지만, jwt 토큰 방식의 경우 SecurityConfig에 아래와 같이 설정하여 세션을 사용하지 않음

    ```java
    public class SecurityConfig {
    
    
        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity, 
            return httpSecurity
            		...
                    .sessionManagement(management ->      management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
    				...
    ```

- **Seucrity Context를 관리하는 것이 `SecurityContextPersistenceFilter` 인데 해당 필터는 SecurityFilterChain이 끝나는 시점(response가 반환되는)에 Seucrity Context를 clear하는 로직이 포함 되어 있음 **
- **즉, 토큰 인증 방식을 사용할 경우 하나의 request - response가 완료되는 시점에 Security Context가 비워짐**