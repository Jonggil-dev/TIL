# Spring Security FilterChain 예외 처리 방법

### 1. 개요

- **필터 체인에서 발생한 예외는 `@RestControllerAdvice`에 정의된 예외 처리기에서 잡히지 않음. **
- **이는 필터가 `DispatcherServlet` 에 요청이 도달하기 전에 실행 되기 때문**
-  즉, 필터 단계에서 발생하는 예외는 `DispatcherServlet`과 그 이후 단계(컨트롤러, 서비스 등)에서 설정한 예외 처리 로직에 의해 처리되지 않음

> 참고
>
> 1. `@RestControllerAdvice`는 `DispatcherServlet`을 통해 처리되는 요청에서 발생하는 예외를 잡아 처리 함. 필터에서 발생한 예외는 이 단계에 도달하기 전에 이미 발생하고 처리되므로, `@RestControllerAdvice`에 의해 처리되지 않음
> 2. `FilterChain`에서 발생한 예외는 보안 관련 예외이기 때문에, 시스템의 무결성과 사용자의 보안을 지키기 위해 빠른 응답이 필요함. 이를 위해 필터 체인을 역순으로 거슬러 올라가지 않고 WAS를 통해 바로 클라이언트에게 응답을 전송하는 방식을 사용함.

### 2. 필터에서 발생한 예외 처리 방법

#### (1) 필터 내에서 직접 예외 처리

- 필터 내에서 `try-catch` 블록을 사용하여 예외를 직접 처리하고 적절한 응답을 클라이언트에게 직접 반환. 이 방법은 필터의 실행이 완전히 독립적이며, `DispatcherServlet` 또는 다른 스프링 컴포넌트와의 상호작용 없이 필터 내에서 모든 예외를 처리할 수 있음

- 예시

  ```java
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
      try {
          chain.doFilter(request, response);
      } catch (Exception e) {
          HttpServletResponse httpResponse = (HttpServletResponse) response;
          httpResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
          httpResponse.setContentType("application/json");
          httpResponse.getWriter().write("{\"error\": \"Exception handled in filter: " + e.getMessage() + "\"}");
          httpResponse.getWriter().flush();
      }
  }
  ```

#### (2) 인증/인가 로직 수행 후 발생한 예외 처리 

- `AuthenticationEntryPoint`
  - **`AuthenticationEntryPoint`는 인증되지 않은 사용자가 보호된 자원에 접근할 시 호출됨(AuthenticationException에 대해서 동작함)**
  -  로그인이 필요한 페이지에 접근하는 상황에서, 사용자가 인증되지 않은 상태일 경우에 동작

- `AccessDeniedHandler`
  - `AccessDeniedHandler`는 사용자가 인증은 되었지만, 해당 요청에 대한 충분한 권한이 없는 경우 호출됨**(AccessDeniedException에 대해서 동작함)**
  - 예를 들어, 특정 역할이나 권한을 가진 사용자만이 접근할 수 있는 페이지에 접근하려고 할 때 해당 사용자의 권한이 부족하다면 `AccessDeniedHandler`가 동작

- 예시

  - `CustomAuthenticationEntryPoint`

    ```java
    public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    
        private final ObjectMapper objectMapper;
    
        @Override
        public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json; charset=UTF-8");
            ApiResponseDto<String> apiResponse = new ApiResponseDto<>(HttpServletResponse.SC_UNAUTHORIZED, AuthErrorCode.INVALID_AUTHENTICATION.getMessage(), null);
            response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
        }
    }
    ```
    
  - `SecurityConfig`에 `CustomAuthenticationEntryPoint` 등록
  
    ```java
    public class SecurityConfig {
    
        @Bean
        public SecurityFilterChain filterChain(HttpSecurity httpSecurity, AuthenticationManager authenticationManager) throws Exception {
    
            LoginIdAuthenticationFilter(authenticationManager, customAuthenticationSuccessHandler, objectMapper);
    				.
                    .
                    .
                   중략
    
            return httpSecurity
                    .exceptionHandling((exceptionConfig) ->
                    exceptionConfig.authenticationEntryPoint(customAuthenticationEntryPoint))
                    .build();
        }
    }
    ```

#### (3) 전역 예외 처리기 사용

전역 예외 처리기를 사용하려면, 필터의 예외를 스프링 컨텍스트가 인식할 수 있도록 설정해야 함. 이를 위해 `DelegatingFilterProxy`를 사용하여 필터를 스프링 관리 빈으로 등록하고, 필터에서 발생한 예외를 스프링의 예외 처리 메커니즘으로 전달할 수 있습니다. 하지만 이 방법은 보통 필터의 설정과 구현에 따라 제한적일 수 있으며, 복잡한 설정이 필요할 수 있음