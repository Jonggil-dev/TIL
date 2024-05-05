# Spring Secuirty FilterChain 작동 방식

- 기본적으로 모든 HTTP 요청은 구성된 모든 필터 체인을 거쳐 가게 되어 있음
- 하지만, 각 필터 내에서 정의된 조건에 따라 특정 로직을 실행하거나, 그저 단순히 요청을 다음 필터로 넘기거나 하는 거임
- **컨트롤러 처럼 특정 요청에 대해 특정 필터가 매핑되어 동작하는게 아님. 즉, 모든 요청은 필터체인에 구성된 모든 필터를 거쳐가지만 필터를 거칠 때 각 필터에 작성된 조건에 맞게 동작하는 것 뿐임 **

### 주요 포인트:
- **모든 필터의 실행**: 모든 요청은 필터 체인을 거치며, 각 필터는 모든 요청에 대해 각 필터에서 조건을 평가합니다.
- **조건적 로직 실행**: 각 필터는 설정된 조건에 따라 요청을 처리하거나 다음 필터로 넘깁니다. 예를 들어, `JwtAuthenticationFilter`는 `Authorization` 헤더에 JWT 토큰이 있는지 확인하고, 토큰이 있다면 인증을 시도합니다. 토큰이 없다면, 필터는 아무런 인증 처리를 하지 않고 요청을 다음 필터로 넘길 수 있음

### 참고

- ```java
  package com.e102.simcheonge_server.domain.auth.security.jwt;
  
  import jakarta.servlet.FilterChain;
  import jakarta.servlet.ServletException;
  import jakarta.servlet.ServletRequest;
  import jakarta.servlet.ServletResponse;
  import jakarta.servlet.http.HttpServletRequest;
  import lombok.RequiredArgsConstructor;
  import org.springframework.security.core.Authentication;
  import org.springframework.security.core.context.SecurityContextHolder;
  import org.springframework.util.StringUtils;
  import org.springframework.web.filter.GenericFilterBean;
  
  import java.io.IOException;
  
  @RequiredArgsConstructor
  public class JwtAuthenticationFilter extends GenericFilterBean {
      private final JwtTokenProvider jwtTokenProvider;
  
      @Override
      public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
          // 1. Request Header에서 JWT 토큰 추출
          String token = resolveToken((HttpServletRequest) request);
  
          // 2. validateToken으로 토큰 유효성 검사
          if (token != null && jwtTokenProvider.validateToken(token)) {
              // 토큰이 유효할 경우 토큰에서 Authentication 객체를 가지고 와서 SecurityContext에 저장
              Authentication authentication = jwtTokenProvider.getAuthentication(token);
              SecurityContextHolder.getContext().setAuthentication(authentication);
          }
          chain.doFilter(request, response);
      }
  
      // Request Header에서 토큰 정보 추출
      private String resolveToken(HttpServletRequest request) {
          String bearerToken = request.getHeader("Authorization");
          if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer")) {
              return bearerToken.substring(7);
          }
          return null;
      }
  }
  ```

  - 위 코드에서 `chain.doFilter`의 역할이 다음 필터체인으로 요청과 응답을 넘긴다는 의미임

  - `JwtAuthenticationFilter`는 `SecurityConfig`에서 `UsernamePasswordAuthenticationFilter`의 앞에 설정되어 있어서 결국 `chain.doFilter`는 `UsernamePasswordAuthenticationFilter`로 넘기겠다는 소리임

  

- ```java
  @Service
  @RequiredArgsConstructor
  @Transactional(readOnly = true)
  public class AuthService {
  
      private final UserRepository userRepository;
      private final JwtTokenProvider jwtTokenProvider;
      private final AuthenticationManagerBuilder authenticationManagerBuilder;
  
      @Transactional
      public LoginResponse login(LoginRequest loginRequest) {
          // 1. username + password 를 기반으로 Authentication 객체 생성
          // 이때 authentication은 인증 여부를 확인하는 authenticated 값이 false
          UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginRequest.getUserLoginId(), loginRequest.getUserPassword());
  
          // 2. 실제 검증. authenticate() 메서드를 통해 요청된 Member 에 대한 검증 진행
          // authenticate 메서드가 실행될 때 CustomUserDetailsService 에서 만든 loadUserByUsername 메서드 실행
          Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
  
          // 3. 인증 정보를 기반으로 JWT 토큰 생성
          JwtToken jwtToken = jwtTokenProvider.generateToken(authentication);
  
          String userLoginId = loginRequest.getUserLoginId();
          Optional<User> userOptional  = userRepository.findByUserLoginId(userLoginId);
          User user = userOptional.get();
  
          return LoginResponse.builder()
                  .userID(user.getUserId())
                  .userLoginId(user.getUserLoginId())
                  .userNickname(user.getUserNickname())
                  .accessToken(jwtToken.getAccessToken())
                  .refreshToken(jwtToken.getRefreshToken())
                  .build();
  
      }
  }
  ```

  - 위 코드는 `AuthController`에 의해 실행되는 `AuthService`의 로그인 로직임
  - 위 코드 중 `Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);` 이 코드는  `AuthenticationManger`를 호출하여 인증 과정을 수행하는 거임
  - 원래 `AuthenticationManger`는 `AuthenticationFilter`에서 인증 과정을 직접 수행하는 객체인데, 위 코드처럼 작성하면 `AuthenticationFilter`를 사용해서 `AuthenticationManger`가 실행되는게 아님. 그냥 별개로 `AuthenticationManger` 객체만 들고와서 인증 로직을 수행하는 거임
