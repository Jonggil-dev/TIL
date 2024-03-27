# Spring Security + JJWT 예시 코드

### 0. 참고자료

- https://suddiyo.tistory.com/entry/Spring-Spring-Security-JWT-%EB%A1%9C%EA%B7%B8%EC%9D%B8-%EA%B5%AC%ED%98%84%ED%95%98%EA%B8%B0-2

<hr>

### **1. 의존성 추가**

-  `build.gradle`에 Spring Security와 JJWT 관련 의존성을 추가합니다.

  ```groovy
  // Spring Security
  implementation 'org.springframework.boot:spring-boot-starter-security'
  
  // jwt
  implementation 'io.jsonwebtoken:jjwt-api:0.11.5'
  runtimeOnly 'io.jsonwebtoken:jjwt-impl:0.11.5'
  runtimeOnly 'io.jsonwebtoken:jjwt-jackson:0.11.5'
  ```

### **2. Secret Key 설정**

- Secret Key 생성

  - Git Bahs에 명령어 입력

    ```shell
    openssl rand -hex 64
    ```

- `application-secret.properties` 파일 생성 및 `Secret Key` 작성

  ```properties
  com.e102.simcheonge-server.secret-key="xxxxx"
  ```

- `application.properties`에 secret 프로파일을 포함하겠다는 코드 작성

  - `spring.profiles.include = secret` 작성

### 3.**Spring config 작성**

- Spring Security를 사용하여 API 요청에 대한 인증 및 권한 부여를 설정. 인증 방식을 정의하고, 어떤 HTTP 요청들이 인증을 필요로 하는지, 어떤 요청들이 허용되는지를 결정하는 것
- `SecurityConfig.java` 작성

```java
package com.e102.simcheonge_server.domain.auth.config;

import com.e102.simcheonge_server.domain.auth.security.jwt.JwtAuthenticationFilter;
import com.e102.simcheonge_server.domain.auth.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                // REST API이므로 basic auth 및 csrf 보안을 사용하지 않음
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                // JWT를 사용하기 때문에 세션을 사용하지 않음
                .sessionManagement(management ->
                        management.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션 정책을 STATELESS로 설정
                .authorizeHttpRequests(authorize -> authorize
                // 해당 API에 대해서는 모든 요청을 허가
                .requestMatchers(HttpMethod.GET, "/posts")
                        .permitAll() // GET 메소드에 대한 /post 요청을 인증 없이 허용
                .requestMatchers("/users/signup", "/users/check-nickname", "/users/check-loginId", "/auth/login", "/policy/**", "/news/**", "/economicword", "/auth/reissue")
                        .permitAll()
//              // USER 권한이 있어야 요청할 수 있음
//                .requestMatchers("/members/test").hasRole("USER")
                // 이 밖에 모든 요청에 대해서 인증을 필요로 한다는 설정
                .anyRequest().authenticated())
                // JWT 인증을 위하여 직접 구현한 필터를 UsernamePasswordAuthenticationFilter 전에 실행
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class).build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // BCrypt Encoder 사용
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
```



### 6. LoginRequest작성

- 로그인 Dto 클래스
- `LoginRequest.java` 작성

```java
package com.e102.simcheonge_server.domain.auth.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginRequest {
    private String userLoginId;
    private String userPassword;
}
```



### 7. AuthController 작성

- `AuthController.java` 작성

  ```java
  package com.e102.simcheonge_server.domain.auth.controller;
  
  
  import com.e102.simcheonge_server.common.util.ResponseUtil;
  import com.e102.simcheonge_server.domain.auth.dto.JwtToken;
  import com.e102.simcheonge_server.domain.auth.dto.request.LogoutRequest;
  import com.e102.simcheonge_server.domain.auth.security.jwt.JwtTokenProvider;
  import com.e102.simcheonge_server.domain.auth.security.jwt.JwtUtil;
  import com.e102.simcheonge_server.domain.auth.service.AuthService;
  import com.e102.simcheonge_server.domain.auth.dto.request.LoginRequest;
  import com.e102.simcheonge_server.domain.user.entity.User;
  import com.e102.simcheonge_server.domain.user.utill.UserUtil;
  import lombok.RequiredArgsConstructor;
  import org.springframework.http.HttpStatus;
  import org.springframework.http.ResponseEntity;
  import org.springframework.security.core.annotation.AuthenticationPrincipal;
  import org.springframework.security.core.userdetails.UserDetails;
  import org.springframework.web.bind.annotation.PostMapping;
  import org.springframework.web.bind.annotation.RequestBody;
  import org.springframework.web.bind.annotation.RequestMapping;
  import org.springframework.web.bind.annotation.RestController;
  
  
  @RestController
  @RequiredArgsConstructor
  @RequestMapping("/auth")
  public class AuthController {
  
      private final AuthService authService;
      private final JwtUtil jwtUtil;
      private final JwtTokenProvider jwtTokenProvider;
  
      @PostMapping("/login")
  
      public ResponseEntity<?> signIn(@RequestBody LoginRequest loginRequest)
      {
          return ResponseUtil.buildBasicResponse(HttpStatus.OK, authService.login(loginRequest));
      }
  
      @PostMapping("/logout")
      public ResponseEntity<?> signOut(@RequestBody LogoutRequest logoutRequest, @AuthenticationPrincipal UserDetails userDetails) {
          User user = UserUtil.getUserFromUserDetails(userDetails);
          jwtUtil.invalidateRefreshToken(user.getUserLoginId());
          return ResponseUtil.buildBasicResponse(HttpStatus.OK,"로그아웃 되었습니다.");
      }
  
      @PostMapping("/reissue")
      public ResponseEntity<?> reissue(@RequestBody JwtToken jwtToken) {
          JwtToken newToken = jwtTokenProvider.reissueToken(jwtToken.getRefreshToken());
          return ResponseUtil.buildBasicResponse(HttpStatus.OK, newToken);
      }
  }
  ```
  
  

### 8. 