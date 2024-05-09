# Spring Security FilterChain 예외 처리 방법

### 1. 개요

필터 체인에서 발생한 예외는 `@RestControllerAdvice`에 정의된 예외 처리기에서 잡히지 않음. 이는 필터와 스프링의 `DispatcherServlet` 간의 처리 순서와 예외 처리 범위 차이 때문

### 2. 예외 처리 순서와 범위
1. **필터 실행**: 필터는 `DispatcherServlet`에 요청이 도달하기 전에 실행 됨. 따라서 필터에서 발생한 예외는 `DispatcherServlet`이 처리할 수 있는 범위 밖에 있음. 즉, 필터 단계에서 발생하는 예외는 `DispatcherServlet`과 그 이후 단계(컨트롤러, 서비스 등)에서 설정한 예외 처리 로직에 의해 처리되지 않음

2. **@RestControllerAdvice 실행**: `@RestControllerAdvice`는 `DispatcherServlet`을 통해 처리되는 요청에서 발생하는 예외를 잡아 처리 함. 필터에서 발생한 예외는 이 단계에 도달하기 전에 이미 발생하고 처리되므로, `@RestControllerAdvice`에 의해 처리되지 않음.

### 3. 필터에서 발생한 예외 처리 방법

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



#### (2) 전역 예외 처리기 사용

전역 예외 처리기를 사용하려면, 필터의 예외를 스프링 컨텍스트가 인식할 수 있도록 설정해야 함. 이를 위해 `DelegatingFilterProxy`를 사용하여 필터를 스프링 관리 빈으로 등록하고, 필터에서 발생한 예외를 스프링의 예외 처리 메커니즘으로 전달할 수 있습니다. 하지만 이 방법은 보통 필터의 설정과 구현에 따라 제한적일 수 있으며, 복잡한 설정이 필요할 수 있음



### 4. 인증/인가 로직 수행 후 발생한 예외 처리 

#### (1) `CustomAuthenticationEntryPoint`

`AuthenticationEntryPoint`는 인증되지 않은 사용자가 보호된 자원에 접근할 시 호출됩니다. 이는 주로 로그인이 필요한 페이지에 접근하려 할 때, 사용자가 인증되지 않은 상태일 경우에 동작하며, 사용자를 로그인 페이지로 리다이렉트하거나, 401 Unauthorized 응답을 반환하는 등의 처리를 담당합니다. 이 컴포넌트는 사용자가 아직 인증 과정을 거치지 않았을 때 필요한 로직을 수행합니다.

#### (2) `CustomAccessDeniedHandler`

`AccessDeniedHandler`는 사용자가 인증은 되었지만, 해당 요청에 대한 충분한 권한이 없는 경우 호출됩니다. 예를 들어, 특정 역할이나 권한을 가진 사용자만이 접근할 수 있는 페이지에 접근하려고 할 때 해당 사용자의 권한이 부족하다면 `AccessDeniedHandler`가 동작합니다. 이는 주로 HTTP 403 Forbidden 응답을 처리하는 데 사용됩니다.

#### (3) 두 핸들러의 필요성

두 핸들러는 서로 다른 시나리오를 다루므로, 완전한 보안 시스템을 위해서는 둘 다 설정하는 것이 일반적입니다. `AuthenticationEntryPoint`가 없으면, 인증되지 않은 사용자가 보호된 자원에 접근했을 때 적절한 안내나 응답 없이 접근이 거부되기만 할 것이며, 이는 사용자 경험 측면에서 부적절할 수 있습니다. 반면, `AccessDeniedHandler`는 이미 인증된 사용자의 권한을 체크하여 추가적인 보안 조치를 취합니다.

#### (4) 예시

- `CustomAuthenticationEntryPoint`

  ```java
  package com.example.icecream.common.auth.handler;
  
  import com.example.icecream.common.auth.error.AuthErrorCode;
  import com.example.icecream.common.dto.ApiResponseDto;
  import com.fasterxml.jackson.databind.ObjectMapper;
  import lombok.RequiredArgsConstructor;
  import org.springframework.security.core.AuthenticationException;
  import org.springframework.security.web.AuthenticationEntryPoint;
  import jakarta.servlet.http.HttpServletRequest;
  import jakarta.servlet.http.HttpServletResponse;
  import org.springframework.stereotype.Component;
  
  import java.io.IOException;
  
  @Component
  @RequiredArgsConstructor
  public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
  
      private final ObjectMapper objectMapper;
  
      @Override
      public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
          response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
          response.setContentType("application/json; charset=UTF-8");
          ApiResponseDto<String> apiResponse = new ApiResponseDto<>(HttpServletResponse.SC_UNAUTHORIZED, AuthErrorCode.INVALID_TOKEN.getMessage(), null);
          response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
      }
  }
  ```

- `SecurityConfig`에 `CustomAuthenticationEntryPoint` 등록

  ```java
  package com.example.icecream.common.config;
  
  import com.example.icecream.common.auth.filter.JwtAuthenticationFilter;
  import com.example.icecream.common.auth.filter.LoginIdAuthenticationFilter;
  import com.example.icecream.common.auth.handler.CustomAuthenticationEntryPoint;
  import com.example.icecream.common.auth.handler.CustomAuthenticationSuccessHandler;
  import com.example.icecream.common.auth.service.CustomUserDetailsService;
  
  import com.fasterxml.jackson.databind.ObjectMapper;
  import jakarta.servlet.http.HttpServletResponse;
  import lombok.RequiredArgsConstructor;
  
  import org.springframework.context.annotation.Bean;
  import org.springframework.context.annotation.Configuration;
  import org.springframework.http.HttpMethod;
  import org.springframework.security.authentication.AuthenticationManager;
  import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
  import org.springframework.security.config.annotation.web.builders.HttpSecurity;
  import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
  import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
  import org.springframework.security.config.http.SessionCreationPolicy;
  
  import org.springframework.security.web.SecurityFilterChain;
  import org.springframework.security.crypto.factory.PasswordEncoderFactories;
  import org.springframework.security.crypto.password.PasswordEncoder;
  import org.springframework.security.web.access.AccessDeniedHandler;
  import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
  
  @Configuration
  @EnableWebSecurity
  @RequiredArgsConstructor
  public class SecurityConfig {
  
      private final ObjectMapper objectMapper;
      private final JwtAuthenticationFilter jwtAuthenticationFilter;
      private final CustomUserDetailsService customUserDetailService;
      private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;
      private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
  
  
      @Bean
      public SecurityFilterChain filterChain(HttpSecurity httpSecurity, AuthenticationManager authenticationManager) throws Exception {
          LoginIdAuthenticationFilter loginIdAuthenticationFilter = new LoginIdAuthenticationFilter(authenticationManager, customAuthenticationSuccessHandler, objectMapper);
  
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
  
      @Bean
      public PasswordEncoder passwordEncoder() {
          return PasswordEncoderFactories.createDelegatingPasswordEncoder();
      }
  
      @Bean
      public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
          AuthenticationManagerBuilder authManagerBuilder =  http.getSharedObject(AuthenticationManagerBuilder.class);
          authManagerBuilder.userDetailsService(customUserDetailService)
                  .passwordEncoder(passwordEncoder());
          return authManagerBuilder.build();
      }
  }
  ```