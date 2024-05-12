# Spring Secuirty FilterChain 작동 방식

- **기본적으로 모든 HTTP 요청은 구성된 모든 필터 체인을 거쳐 가게 되어 있음. 즉, 컨트롤러 처럼 특정 요청에 대해 특정 필터가 매핑되어 동작하는게 아님**
- **이 때, 각 필터 내에서 작성된 조건에 따라 특정 로직을 실행하거나, 단순히 요청을 다음 필터로 넘기기 때문에 특정 요청에 대해 특정 필터만 활성화 되는것 처럼 보이는 것 뿐임**
- **즉, 모든 요청은 필터체인에 구성된 모든 필터를 거쳐가지만, 필터를 거칠 때 각 필터에 작성된 로직에 맞게 동작하는 방식임 **

### 주요 포인트:
- **모든 필터의 실행**: 모든 요청은 필터 체인을 거치며, 각 필터는 모든 요청에 대해 각 필터에서 조건을 평가합니다.
- **조건적 로직 실행**: 각 필터는 설정된 조건에 따라 요청을 처리하거나 다음 필터로 넘깁니다. 예를 들어, `JwtAuthenticationFilter`는 `Authorization` 헤더에 JWT 토큰이 있는지 확인하고, 토큰이 있다면 인증을 시도합니다. 토큰이 없다면, 필터는 아무런 인증 처리를 하지 않고 요청을 다음 필터로 넘길 수 있음

### 참고

- ```java
  package com.example.icecream.domain.user.auth.config;
  
  import com.example.icecream.domain.user.auth.filter.JwtAuthenticationFilter;
  import com.example.icecream.domain.user.auth.filter.LoginIdAuthenticationFilter;
  import com.example.icecream.domain.user.auth.handler.CustomAuthenticationEntryPoint;
  import com.example.icecream.domain.user.auth.handler.CustomAuthenticationSuccessHandler;
  import com.example.icecream.domain.user.auth.service.CustomUserDetailsService;
  
  import com.example.icecream.domain.user.auth.util.JwtUtil;
  import com.fasterxml.jackson.databind.ObjectMapper;
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
  import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
  
  @Configuration
  @EnableWebSecurity
  @RequiredArgsConstructor
  public class SecurityConfig {
  
      private final ObjectMapper objectMapper;
      private final JwtUtil jwtUtil;
      private final CustomUserDetailsService customUserDetailService;
      private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;
      private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
  
      @Bean
      public SecurityFilterChain filterChain(HttpSecurity httpSecurity, AuthenticationManager authenticationManager) throws Exception {
          LoginIdAuthenticationFilter loginIdAuthenticationFilter = new LoginIdAuthenticationFilter(authenticationManager, customAuthenticationSuccessHandler, objectMapper);
          JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtUtil);
  
          return httpSecurity
                  .formLogin(AbstractHttpConfigurer::disable)
                  .httpBasic(AbstractHttpConfigurer::disable)
                  .csrf(AbstractHttpConfigurer::disable)
                  .sessionManagement(management ->
                          management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                  .authorizeHttpRequests(authorize -> authorize
                          .requestMatchers(HttpMethod.POST, "/users")
                          .permitAll()
                          .requestMatchers("/users/check", "/auth/login", "/auth/device/login","/auth/reissue", "/error")
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
  
  - **위 코드에서 `chain.doFilter`의 역할이 다음 필터체인으로 요청과 응답을 넘긴다는 의미임**
  
  - `JwtAuthenticationFilter`는 `SecurityConfig`에서 `UsernamePasswordAuthenticationFilter`의 앞에 설정되어 있어서 결국 `chain.doFilter`는 `UsernamePasswordAuthenticationFilter`로 넘기겠다는 소리임
  
  
  
- ```java
  package com.example.icecream.domain.user.auth.service;
  
  import com.example.icecream.domain.user.auth.error.AuthErrorCode;
  import com.example.icecream.domain.user.auth.util.JwtUtil;
  import com.example.icecream.common.exception.NotFoundException;
  import com.example.icecream.domain.notification.dto.LoginRequestDto;
  import com.example.icecream.domain.notification.service.NotificationService;
  import com.example.icecream.domain.user.entity.User;
  import com.example.icecream.domain.user.repository.ParentChildMappingRepository;
  import com.example.icecream.domain.user.repository.UserRepository;
  import com.example.icecream.domain.user.auth.dto.*;
  import lombok.RequiredArgsConstructor;
  import org.springframework.stereotype.Service;
  import java.util.List;
  
  @Service
  @RequiredArgsConstructor
  public class AuthService {
  
      private final UserRepository userRepository;
      private final ParentChildMappingRepository parentChildMappingRepository;
      private final JwtUtil jwtUtil;
      private final NotificationService notificationService;
  
      public LoginResponseDto deviceLogin(DeviceLoginRequestDto deviceLoginRequestDto) {
  
          User user = userRepository.findByDeviceId(deviceLoginRequestDto.getDeviceId())
                  .orElseThrow(() -> new NotFoundException(AuthErrorCode.USER_NOT_FOUND.getMessage()));
  
          if (user.getIsParent()) {
              if (jwtUtil.validateRefreshToken(deviceLoginRequestDto.getRefreshToken())) {
                  List<User> children = parentChildMappingRepository.findChildrenByParentId(user.getId());
  
                  List<ChildrenResponseDto> childrenResponseDto = children.stream()
                          .map(child -> new ChildrenResponseDto(child.getId(), child.getProfileImage(), child.getUsername(), child.getPhoneNumber()))
                           .toList();
  
                  JwtTokenDto jwtTokenDto = jwtUtil.generateTokenByController(String.valueOf(user.getId()), "ROLE_PARENT");
  
                  LoginRequestDto loginRequestDto = new LoginRequestDto(user.getId(), deviceLoginRequestDto.getFcmToken());
                  notificationService.saveOrUpdateFcmToken(loginRequestDto);
  
                  return ParentLoginResponseDto.builder()
                          .username(user.getUsername())
                          .loginId(user.getLoginId())
                          .phoneNumber(user.getPhoneNumber())
                          .profileImage(user.getProfileImage())
                          .children(childrenResponseDto)
                          .accessToken(jwtTokenDto.getAccessToken())
                          .refreshToken(jwtTokenDto.getRefreshToken())
                          .build();
              }
          }
  
          JwtTokenDto jwtTokenDto = jwtUtil.generateTokenByController(String.valueOf(user.getId()), "ROLE_CHILD");
          LoginRequestDto loginRequestDto = new LoginRequestDto(user.getId(), deviceLoginRequestDto.getFcmToken());
          notificationService.saveOrUpdateFcmToken(loginRequestDto);
  
          return ChildLoginResponseDto.builder()
                  .userId(user.getId())
                  .username(user.getUsername())
                  .phoneNumber(user.getPhoneNumber())
                  .profileImage(user.getProfileImage())
                  .accessToken(jwtTokenDto.getAccessToken())
                  .refreshToken(jwtTokenDto.getRefreshToken())
                  .build();
      }
  }
  ```
  
  - 위 코드는 `AuthController`에 의해 실행되는 `AuthService`의 로그인 로직임
  - 위 코드 중 `Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);` 이 코드는  `AuthenticationManger`를 호출하여 인증 과정을 수행하는 거임
  - 원래 `AuthenticationManger`는 `AuthenticationFilter`에서 인증 과정을 직접 수행하는 객체인데, 위 코드처럼 작성하면 `AuthenticationFilter`를 사용해서 `AuthenticationManger`가 실행되는게 아님. 그냥 별개로 `AuthenticationManger` 객체만 들고와서 인증 로직을 수행하는 거임
