# Spring Secuirty FilterChain 작동 방식

- **기본적으로 모든 HTTP 요청은 구성된 모든 필터 체인을 거쳐 가게 되어 있음. 즉, 컨트롤러 처럼 특정 요청에 대해 특정 필터가 매핑되어 동작하는게 아님**
- **이 때, 각 필터 내에서 작성된 조건에 따라 특정 로직을 실행하거나, 단순히 요청을 다음 필터로 넘기기 때문에 특정 요청에 대해 특정 필터만 활성화 되는것 처럼 보이는 것 뿐임**
- **즉, 모든 요청은 필터체인에 구성된 모든 필터를 거쳐가지만, 필터를 거칠 때 각 필터에 작성된 로직에 맞게 동작하는 방식임 **

### 참고

- ```java
  // 자율 프로젝트 JwtAuthenticationFilter
  
  @RequiredArgsConstructor
  public class JwtAuthenticationFilter extends OncePerRequestFilter {
  				...중략
      @Override
      protected void doFilterInternal(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {
  
          //PermitAll() url에 대해서는 다음 필터로 요청을 바로 넘김
          String requestURI = request.getRequestURI();
          List<String> skipUrls = List.of("/api/users/check", "/api/auth/login", "/api/auth/device/login", "/api/auth/reissue", "/api/error");
          boolean skip = skipUrls.stream().anyMatch(requestURI::equals);
          if ("/api/users".equals(requestURI) && "POST".equalsIgnoreCase(request.getMethod())) {
              skip = true;
          }
          if (skip) {
              filterChain.doFilter(request, response);
              return;
          }
  
          //access_token 검증
          String token = resolveToken(request);
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
  
  
          filterChain.doFilter(request, response);
      }
  
      private String resolveToken(HttpServletRequest request) {
              String bearerToken = request.getHeader("Authorization");
  
              if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer")) {
                  return bearerToken.substring(7);
              }
  
              return null;
      }
  }
  ```
  
  - **위 코드에서 `filterChain.doFilter`의 역할이 다음 필터체인으로 요청과 응답을 넘긴다는 의미임**
  
  - `JwtAuthenticationFilter`는 `SecurityConfig`에서 `UsernamePasswordAuthenticationFilter`의 앞에 설정되어 있어서 결국 `filterChain.doFilter`는 `UsernamePasswordAuthenticationFilter`로 넘기겠다는 소리임
  
  
  
- ```java
  //특화 프로젝트 로그인 로직
  
  @Service
  @RequiredArgsConstructor
  @Transactional(readOnly = true)
  public class AuthService {
  
      private final UserRepository userRepository;
      private final JwtTokenProvider jwtTokenProvider;
      private final AuthenticationManagerBuilder authenticationManagerBuilder;
  
      @Transactional
      public LoginResponse login(LoginRequest loginRequest) {
          UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginRequest.getUserLoginId(), loginRequest.getUserPassword());
  
          Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
  
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
