# Spring Security FilterChain 예외 처리 방법

### 0. 참고

- [ExceptionTranslationFilter 참고](https://yenjjun187.tistory.com/578)
- [ExceptionTranslationFilter, FilterSecurityInterceptor 참고](https://velog.io/@youngerjesus/Spring-Security-%EB%B6%84%EC%84%9D%ED%95%98%EA%B8%B0#exceptiontranslationfilter)



### 1. 개요

- **필터 체인에서 발생한 예외는 `@RestControllerAdvice`에 정의된 예외 처리기에서 잡히지 않음. 이는 필터 체인은 `DispatcherServlet` 에 요청이 도달하기 전에 실행 되기 때문**

  - 즉, 필터 단계에서 발생하는 예외는 `DispatcherServlet`과 그 이후 단계(컨트롤러, 서비스 등)에서 설정한 예외 처리 로직에 의해 처리되지 않음

  > 참고
  >
  > 1. `@RestControllerAdvice`는 `DispatcherServlet`을 통해 처리되는 요청에서 발생하는 예외를 잡아 처리 함. 필터에서 발생한 예외는 이 단계에 도달하기 전에 이미 발생하고 처리되므로, `@RestControllerAdvice`에 의해 처리되지 않음
  > 2. `FilterChain`에서 발생한 예외는 보안 관련 예외이기 때문에, 시스템의 무결성과 사용자의 보안을 지키기 위해 빠른 응답이 필요함. 이를 위해 필터 체인을 역순으로 거슬러 올라가지 않고 WAS를 통해 바로 클라이언트에게 응답을 전송하는 방식을 사용함.

  

### 2. 필터 체인에서 발생한 예외 처리 방법

- **유의해야 할 점은 필터체인 내에서 발생한 모든 인증/인가 예외가 `ExceptionTranslationFilter`에서 처리 되는 것은 아님. `FilterSecurityInterceptor` 에서 발생한 인증/인가 예외에 대해서만 `ExceptionTranslationFilter`에서 처리 된다는 것에 유의**
- **그 중 하나가 `AuthenticationFilter`의 예외를 직접 담당하는 것은 `AuthenticationFailureHandler`임**
  - `AuthenticationFilter` 로직 중에서 발생한 예외를 직접 처리하는 핸들러 (ex. 비밀번호 불일치 등) 
- **`ExceptionTranslationFilter`는 request가 필터 체인의 마지막에 위치한 `FilterSecurityInterceptor`에 도달하고 해당 필터에서 인증/인가 여부를 검사하는 과정에서 발생한 인증/인가 예외를 처리하는 것이라 생각하면 됨**

![Security 예외 처리](https://github.com/user-attachments/assets/61ac64bb-a193-4f1d-91c2-39c46dc42545)


####  **(1) `FilterSecurityInterceptor` 에서 발생한 인증/인가 예외**

- **`FilterSecurityInterceptor`**
  - 필터 체인의 마지막에 위치한 필터로 인가 처리를 통해 리소스에 접근하는 요청에 승인 여부를 결정하는 필터. 
  - 처리하는 방식은 인증 객체가 없이 이 필터 체인에 오면 `AuthenticationException` 이 발생하도록 하고 인증 객체가 있지만 자원에 접근하는 권한이 없다면 `AccessDeniedException` 이 발생함. 이렇게 발생한 예외는 `ExceptionTranslationFilter` 가 처리함.

- **`ExceptionTranslationFilter`**
  - `FilterSecurityInterceptor` 보다 앞쪽에 위치하여 `try - catch` 구문을 통해 `FilterSecurityInterceptor`를 호출하는 방식으로 코드가 구성되어 있음
  - `FilterSecurityInterceptor`가 던지는 **`AuthenticationException, AccessDeniedException`** 예외를 `ExceptionTranslationFilter`가 처리하는 구조
  - `ExceptionTranslationFilter`는 내부적으로
    - 인증 예외 : **`AuthenticationException` 발생 시 `AuthenticationEntryPoint`**를 호출
    - 인가 예외 : **`AccessDeniedException` 발생 시 `AccessDeniedHandler`**를 호출

#### **(2) `FilterSecurityInterceptor`가 아닌 다른 필터에서 발생한 예외**

- **기본적으로 예외는 `throw`를 통해 상위 스택으로만 전파 되므로, `ExceptionTranslationFilter`보다 앞쪽에 위치한 Filter에서 발생한 인증/인가 예외에 대해서는 `ExceptionTranslationFilter`로 전파 되지 않음**
- 결과적으로 `FilterSecurityInterceptor` 외부에서 발생한 예외에 대해서는
  - **`try - catch` 를 이용해 예외 발생시에도 `dofilter()` 메서드를 사용해 다음 filter로 동작을 넘겨버리는 방법**
    - 적절한 인증/인가 로직이 수행되지 않은 상태로 `dofilter()`를 통해`FilterSecurityInterceptor`까지 도달하게 되면 결국 `FilterSecurityInterceptor`에서 인증/인가 예외가 발생할 것이고 `ExceptionTranslationFilter`에 의해 처리 되도록 유도하는 방법
    - 예시) `JwtAuthenticationFilter` 의 토큰 검증 로직 수행에서 발생하는 예외를 catch로 잡아서 그냥 `dofilter()`를  진행 시킴. 그러면 SecurityContext에 정상적인 인증 객체가 등록이 되지 않을 것이고 결국 `FilterSecurityInterceptor`에서 인증/인가 예외가 발생하여 `ExceptionTranslationFilter` 를 통해 처리 되거나 `permitAll() request`라면 `ExceptionTranslationFilter`의 개입 없이 정상적으로 `Controller`로 요청이 전파 됨
  - **예외가 발생하는 필터에서 직접 예외를 처리하는 코드를 구성**
    - 예시) `UsernamePasswordAuthenticationFilter` 의 로직 수행 중에 `AuthenticationException` 서브 클래스 예외가 발생한다면, 해당 에러는 `ExceptionTranslationFilter`에서 처리 되는 것이 아님. **자신의 내부 로직에서 이 예외를 처리하거나, 스프링의 기본 예외 처리 메커니즘에 의해 처리되는 것임**



### 3. 예외 처리 구현 방법

#### (1) 필터 내에서 직접 예외 처리

- 필터 내에서 `try-catch` 블록을 사용하여 예외를 직접 처리하고 적절한 응답을 클라이언트에게 직접 반환. 이 방법은 필터의 실행이 완전히 독립적이며, `DispatcherServlet` 또는 다른 스프링 컴포넌트와의 상호작용 없이 필터 내에서 모든 예외를 처리할 수 있음

- 예시

  ```java
   @Override
      protected void doFilterInternal(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {
  
        				... 중략
                          
          try {
              if (token != null && jwtutil.validateAccessToken(token)) {
                  Authentication authentication = jwtutil.getAuthentication(token);
                  SecurityContextHolder.getContext().setAuthentication(authentication);
              }
          } catch (Exception e) {
              response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
              response.setContentType("application/json; charset=UTF-8");
              ApiResponseDto<String> apiResponse = new ApiResponseDto<>(HttpServletResponse.SC_UNAUTHORIZED, AuthErrorCode.INVALID_TOKEN.getMessage(), null);
              response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
              return;
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
