# Spring Security Filter 중복 등록

### 0. 참고

- [바로가기](https://shanepark.tistory.com/497)

### 1. 문제 상황

- 내가 커스텀해서 작성한 `JwtAuthenticationFilter`의 출력을 확인해 보니까, 한 번의 request에 대해서 해당 필터 로직의 출력이 2번 찍히는 상황이 발생
- Filter를 등록할 때 `SecurityConfig`의 `filterChain` Bean에 `.addFilterBefore()` 를 통해 직접 필터를 등록 한 고,`JwtAuthenticationFilter`자체를 또 `@Copmonent`를 사용해 Bean으로 등록한 것이 Filter의 중복 등록을 초래했음.

### 2. 필터 체인 중복 등록

- `WebSecurityConfigurerAdapter` 구현에서 필터를 직접 추가하는 방식과, 필터를 스프링 빈으로 등록하는 두 가지 방식을 혼용했을 때 발생

- **예시**: 필터를 `@Bean`으로 등록하고, 동시에 `HttpSecurity`의 `addFilterBefore` 또는 `addFilterAfter` 메소드를 사용하여 필터 체인에 추가한 경우, 필터가 두 번 등록될 수 있음

`JwtAuthenticationFilter`를 `@Bean`으로 등록하면서 동시에 `HttpSecurity`를 사용하여 필터 체인에 명시적으로 추가한 경우, 스프링 보안이 필터 체인에 필터를 자동으로 한 번 더 추가할 수 있기 때문입니다. **스프링 시큐리티는 내부적으로 `@Bean`으로 등록된 필터를 자동으로 필터 체인에 추가하려는 로직이 있기 때문에**, 사용자가 직접 추가하면 필터가 두 번 등록되어 동일한 필터가 필터 체인에서 두 번 실행됩니다.

### 3. 해결방법

- **한 가지 방법만 사용**

  - 필터가 자동으로 등록되지 않도록 `JwtAuthenticationFilter`의 `@Component` 어노테이션을 제거

  - 스프링 시큐리티 설정파일에서는 해당 `Filter`를 생성자를 통해 생성해 필터체인에 등록

  ```java
  
  
    ```java
    public class SecurityConfig {
    
        private final ObjectMapper objectMapper;
        private final JwtUtil jwtUtil;
        private final CustomUserDetailsService customUserDetailService;
        private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;
        private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    
    
        @Bean
        public SecurityFilterChain filterChain(HttpSecurity httpSecurity, AuthenticationManager authenticationManager) throws Exception {
            LoginIdAuthenticationFilter loginIdAuthenticationFilter = new LoginIdAuthenticationFilter(authenticationManager, customAuthenticationSuccessHandler, objectMapper);
            JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtUtil, objectMapper);
            return httpSecurity
                    .formLogin(AbstractHttpConfigurer::disable)
                    .httpBasic(AbstractHttpConfigurer::disable)
                    .csrf(AbstractHttpConfigurer::disable)
                    .sessionManagement(management ->
                            management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                    .authorizeHttpRequests(authorize -> authorize
                            .requestMatchers(HttpMethod.POST, "/users")
                            .permitAll()
                            .requestMatchers("/users/check", "/auth/login", "/auth/device/login","auth/reissue")
                            .permitAll()
                            .anyRequest().authenticated())
                    .exceptionHandling((exceptionConfig) ->
                            exceptionConfig.authenticationEntryPoint(customAuthenticationEntryPoint))
                    .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                    .addFilterAt(loginIdAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                    .build();
        }
    ```
  
    
  ```

  

- **조건적 등록**: 필터를 `@Bean`으로 등록할 때, `FilterRegistrationBean`을 사용하여 필터의 자동 등록을 비활성화

  ```java
  @Bean
  public FilterRegistrationBean<JwtAuthenticationFilter> registration(JwtAuthenticationFilter filter) {
      FilterRegistrationBean<JwtAuthenticationFilter> registration = new FilterRegistrationBean<>(filter);
      registration.setEnabled(false); // 필터를 필터 체인에 자동 등록하지 않도록 설정
      return registration;
  }
  ```
