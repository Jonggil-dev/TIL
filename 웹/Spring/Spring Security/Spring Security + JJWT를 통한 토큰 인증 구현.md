# Spring Security + JJWT를 통한 토큰 인증 구현

### 0. 참고자료

- https://www.youtube.com/watch?v=ILWwnL0kvTk&t=589s
- https://wwwaloha.oopy.io/211bf13e-948a-4ba0-b6c1-cfedea148784

<hr>

### **1. 의존성 추가**

-  `build.gradle`에 Spring Security와 JJWT 관련 의존성을 추가합니다.

  ```groovy
  // Spring Security
  implementation 'org.springframework.boot:spring-boot-starter-security'
  
  // jwt
  implementation 'io.jsonwebtoken:jjwt-api:0.12.3'
  runtimeOnly 'io.jsonwebtoken:jjwt-impl:0.12.3'
  runtimeOnly 'io.jsonwebtoken:jjwt-jackson:0.12.3'
  ```



### **2. Secret Key 설정**

- `application-secret.properties` 파일 생성 및 Secret Key 작성

  ```properties
  # HS512 알고리즘의 시크릿키 : 512비트 (64바이트) 이상
  # https://passwords-generator.org/kr/ 
  # ✅ 위 사이트에서 길이:64 로 생성함.
  com.e102.simcheonge_server.secret-key=|+<T%0h;[G97|I$5Lr?h]}`8rUX.7;0gw@bF<R/|"-U0n:_6j={'.T'GHs~<AxU9
  ```

- `application.properties`에 secret 프로파일을 포함하겠다는 코드 작성

  - `spring.profiles.include = secret`  작성



### 3.**Spring config 작성**

- Spring Security를 사용하여 API 요청에 대한 인증 및 권한 부여를 설정. 인증 방식을 정의하고, 어떤 HTTP 요청들이 인증을 필요로 하는지, 어떤 요청들이 허용되는지를 결정하는 것
- `SecurityConfig.java` 작성

```java
package com.e102.simcheonge_server.domain.jwt.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

// Spring-Security 5.4 이하에서 썻던 방법 (이제 안씀)
// @EnableWebSecurity
// public class SecurityConfig extends WebSecurityConfigurerAdapter {
//   @Override
//   protected void configure(HttpSecurity http) throws Exception {
    // 폼 기반 로그인 비활성화
// 	 http.formLogin().disable()

    // HTTP 기본 인증 비활성화
// 	 .httpBasic().disable();

    // CSRF(Cross-Site Request Forgery) 공격 방어 기능 비활성화
// 	  http.csrf().disable();

    // 세션 관리 정책 설정: STATELESS로 설정하면 서버는 세션을 생성하지 않음
    // 세션을 사용하여 인증하지 않고,  JWT 를 사용하여 인증하기 때문에, 세션 불필요
// 	  http.sessionManagement()
// 		 .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
//  }
//}

// 아래는 Spring-Security 5.5 이상에서 쓰는 방식 
@Configuration
@EnableWebSecurity
public class SecurityConfig {

	// OK : (version : after SpringSecurity 5.4 ⬆)
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		// 폼 기반 로그인 비활성화
		http.formLogin(login ->login.disable());							

		// HTTP 기본 인증 비활성화
		http.httpBasic(basic ->basic.disable());

		// CSRF(Cross-Site Request Forgery) 공격 방어 기능 비활성화
		http.csrf(csrf ->csrf.disable());

		// 세션 관리 정책 설정: STATELESS로 설정하면 서버는 세션을 생성하지 않음
	 	// 세션을 사용하여 인증하지 않고,  JWT 를 사용하여 인증하기 때문에, 세션 불필요
		http.sessionManagement(management ->management
			.sessionCreationPolicy(SessionCreationPolicy.STATELESS));


		// 구성이 완료된 SecurityFilterChain을 반환합니다.
		return http.build();
	}

	
	

}
```



### 4. Security Constants 작성

- `SecurityConstants.java` 작성

  ```java
  package com.e102.simcheonge_server.domain.jwt.constants;
  
  // 해당 클래스는 Spring Security 및 JWT 관련 상수를 정의한 상수 클래스입니다.
  /**
   * HTTP
   *     headers : {
   *			Authorization : Bearer ${jwt}
   * 	   }
   */
  public final class SecurityConstants {
  
      // JWT 토큰을 HTTP 헤더에서 식별하는 데 사용되는 헤더 이름
      public static final String TOKEN_HEADER = "Authorization";
  
      // JWT 토큰의 접두사. 일반적으로 "Bearer " 다음에 실제 토큰이 옵니다.
      public static final String TOKEN_PREFIX = "Bearer ";
  
      // JWT 토큰의 타입을 나타내는 상수
      public static final String TOKEN_TYPE = "JWT";
      
      // 이 클래스를 final로 선언하여 상속을 방지하고, 상수만을 정의하도록 만듭니다.
  }
  ```



### 5. JwtProp 작성

- `application.properties`의 `SecretKey`를 가져오는 클래스

- `JwtProps` 작성

  ```java
  package com.e102.simcheonge_server.domain.jwt.prop;
  
  import org.springframework.boot.context.properties.ConfigurationProperties;
  import org.springframework.stereotype.Component;
  import lombok.Data;
  
  // 해당 클래스는 Spring Boot의 `@ConfigurationProperties`
  // 어노테이션을 사용하여, application.properties(속성 설정 파일) 로부터
  // JWT 관련 프로퍼티를 관리하는 프로퍼티 클래스입니다.
  @Data
  @Component
  @ConfigurationProperties("com.e102.simcheonge_server") // 괄호 내부 application-secret.properties의 Secretkey 앞에 작성된 경로 작성
  public class JwtProps {
  
      // com.e102.simcheonge_server.secretKey로 지정된 프로퍼티 값을 주입받는 필드
      // com.e102.simcheonge_server.secret-key ➡ secretKey : {인코딩된 시크릿 키}
      private String secretKey;
  
  
  }
  ```



### 6. LoginRequest작성

- 로그인 Dto 클래스
- `LoginRequest.java` 작성

```java
package com.e102.simcheonge_server.domain.user.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginReqeust {
    private String userLoginId;
    private String userPassword;
}
```



### 7. Login 관련 Controller 작성

- `LoginController.java` 작성

  ```java
  package com.e102.simcheonge_server.domain.user.controller;
  
  import java.util.ArrayList;
  import java.util.Date;
  import java.util.List;
  
  import org.springframework.beans.factory.annotation.Autowired;
  import org.springframework.http.HttpStatus;
  import org.springframework.http.ResponseEntity;
  import org.springframework.web.bind.annotation.GetMapping;
  import org.springframework.web.bind.annotation.PostMapping;
  import org.springframework.web.bind.annotation.RequestBody;
  import org.springframework.web.bind.annotation.RequestHeader;
  import org.springframework.web.bind.annotation.RestController;
  
  import com.joeun.jwt.constants.SecurityConstants;
  import com.joeun.jwt.domain.AuthenticationRequest;
  import com.joeun.jwt.prop.JwtProps;
  
  import io.jsonwebtoken.Claims;
  import io.jsonwebtoken.Jws;
  import io.jsonwebtoken.Jwts;
  import io.jsonwebtoken.security.Keys;  // 추가: 새로운 방식으로 SecretKey 생성을 위한 클래스
  import lombok.extern.slf4j.Slf4j;
  
  
  @Slf4j
  @RestController
  public class LoginController {
  
      @Autowired
      private JwtProps jwtProps;
  
      /**
       * 👩‍💼➡🔐 JWT 을 생성하는 Login 요청
       * [GET] - /login
       * body : 
              {
                  "username" : "joeun",
                  "password" : "123456"
              }
       * @param authReq
       * @return
       */
      @PostMapping("login")
      public ResponseEntity<?> login(@RequestBody AuthenticationRequest authReq) {
          // 사용자로부터 전달받은 인증 정보
          String username = authReq.getUsername();
          String password = authReq.getPassword();
  
          log.info("username : " + username);
          log.info("password : " + password);
  
          // 사용자 역할 정보
          List<String> roles = new ArrayList<>();
          roles.add("ROLE_USER");
          roles.add("ROLE_ADMIN");
  
          // 서명에 사용할 키 생성 (새로운 방식)
          String secretKey = jwtProps.getSecretKey();
          byte[] signingKey = jwtProps.getSecretKey().getBytes();
  
          log.info("secretKey : " + secretKey);
          log.info("signingKey : " + signingKey);
  
          // JWT 토큰 생성
          String jwt = Jwts.builder()
                  .signWith(Keys.hmacShaKeyFor(signingKey), Jwts.SIG.HS512)      // 서명에 사용할 키와 알고리즘 설정
                  // .setHeaderParam("typ", SecurityConstants.TOKEN_TYPE)        // deprecated (version: before 1.0)
                  .header()                                                      // update (version : after 1.0)
                      .add("typ", SecurityConstants.TOKEN_TYPE)              // 헤더 설정
                  .and()
                  .expiration(new Date(System.currentTimeMillis() + 864000000))  // 토큰 만료 시간 설정 (10일)
                  .claim("uid", username)                                   // 클레임 설정: 사용자 아이디
                  .claim("rol", roles)                                      // 클레임 설정: 역할 정보
                  .compact();                                                    // 최종적으로 토큰 생성
  
          log.info("jwt : " + jwt);
  
          // 생성된 토큰을 클라이언트에게 반환
          return new ResponseEntity<String>(jwt, HttpStatus.OK);
      }
  
  
      /**
       * 🔐➡👩‍💼 JWT 를 해석하는 요청
       * 
       * @param header
       * @return
       */
      @GetMapping("/user/info")
      public ResponseEntity<String> userInfo(@RequestHeader(name="Authorization") String header) {
  
          log.info("===== header =====");
          log.info("Authorization : " + header);
  
          String jwt = header.substring(7);           // "Bearer " + jwt  ➡ jwt 추출
  
          log.info("jwt : " + jwt);
  
          String secretKey = jwtProps.getSecretKey();
          byte[] signingKey = jwtProps.getSecretKey().getBytes();
  
          log.info("secretKey : " + secretKey);
          log.info("signingKey : " + signingKey);
  
          // TODO : deprecated 업애기 (version: before 1.0)
          // Jws<Claims> parsedToken = Jwts.parser()
          //                                 .setSigningKey(signingKey)
          //                                 .build()
          //                                 .parseClaimsJws(jwt);
  
          // OK : deprecated 된 코드 업데이트 (version : after 1.0)
          // - setSigningKey(byte[]) ➡ verifyWith(SecretKey)
          // - parseClaimsJws(CharSequence) ➡ parseSignedClaims(CharSequence)
          Jws<Claims> parsedToken = Jwts.parser()
                                          .verifyWith(Keys.hmacShaKeyFor(signingKey))
                                          .build()
                                          .parseSignedClaims(jwt);
          log.info("parsedToken : " + parsedToken);
  
          
          String username = parsedToken.getPayload().get("uid").toString();
          log.info("username : " + username);
  
          Claims claims = parsedToken.getPayload();
          Object roles = claims.get("rol");
          log.info("roles : " + roles);
  
          return new ResponseEntity<String>(parsedToken.toString(), HttpStatus.OK);
      }
   
  }
  ```

  