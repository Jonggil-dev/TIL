# Spring Security 동작 원리(JWT 토큰)

### !!필독!!

- 로그인 인증 과정에서 사용자의 인증 절차까지는 기본적으로 쿠키-세션의 절차와 동일함

- 차이는 로그인 인증이 성공적으로 완료되었을 때 세션을 생성하느냐 JWT 토큰을 생성하느냐의 차이임

- 로그인 이후 인증 처리 과정은 차이가 있음 

- **이 파일의 동작 과정들은 Spring Security에서 제공하는 로그인 기능 URL로 요청이 갔을 때 일어나는 동작 과정을 설명한 거임**

- **프로젝트에서 구현한 방법**

  - 내가 프로젝트에서 구현한 jwt 토큰 로그인/인증의 경우는 Spring Security에서 제공하는 Login 서비스(Form 로그인)를 사용하지 않았음.
  - 즉, 이 파일의 동작 과정에 따라서 인증/인가 과정이 처리 된 게 아님
    (잘 모르는 상태로 한 거라 기본 제공하는 Form 로그인을 활용할 생각을 안했음)

  - URL 요청이 Controller에 도달 했을 때 별도 구현한 Login 로직에 의해 처리됨. 이 때 Login 로직은 Spring Security에서 제공하는 인터페이스들을 이용했음. 
  - 결국, 전체적인 과정 자체는 아래 작성한 동작 과정들과 일치하지 않음. 하지만, 부분적으로 보았을 때 사용한 인터페이스에서 일어나는 동작은 같거나 비슷하기 때문에 아래 자료 참고해서 이해하면 됨

- **만약, 프로젝트에서 사용한 로그인에 Spring-Security의 filterchain을 적용하려 했다면, 아래 2가지를 구성해서 도전해볼 수 있을듯?**

  - **SrpingConfig 파일의 filterChain 클래스 작성**
    - `loginProcessingUrl("/loginUrl")` // 로그인 액션 URL 설정

  - **`UsernamePasswordAuthenticationFilter`를 상속받는 커스텀 필터를 구현**
    - 이 필터에서 JSON 데이터를 파싱하도록 `UsernamePasswordAuthenticationToken`을 생성

## 🔑출처 

- https://velog.io/@leeeeeyeon/Spring-Boot-Spring-Security-%EB%8F%99%EC%9E%91-%EC%9B%90%EB%A6%AC
- https://skatpdnjs.tistory.com/41
- https://velog.io/@kyungwoon/Spring-Security-%EB%8F%99%EC%9E%91-%EC%9B%90%EB%A6%AC



## 🔒 Spring Security란?

스프링 기반 어플리케이션의 보안(인증, 권한, 인가)을 담당하는 스프링 하위 프레임워크



### 📌 Spring Security 용어

- 인증 (Authentication): 해당 사용자가 본인인지 확인하는 절차
- 인가 (Authorization): 인증된 사용자가 요청한 자원에 접근 가능한지 결정하는 절차
- 권한: 인증된 주체가 어플리케이션의 동작을 수행할 수 있도록 허락되어 있는지 결정
  - 인증 과정을 통해 주체가 증명된 이후 권한을 부여할 수 있음
  - 웹 요청 권한 / 메서드 호출 및 도메인 인스턴스에 대한 접근 권한 부여로 나뉨

> Spring Security 과정은 크게 인증 → 인가로 진행
> 인증과 인가를 위해 Principal을 아이디로, Credential을 비밀번호로 사용
> 기본적으로 세션-쿠키 방식을 사용



### ✔전체 동작 원리 (인증 관련 아키텍쳐)

**아래 사진의  AuthenticationFilter는 UsernamePasswordAuthenticationFilter을 의미**

![전체 동작 원리](https://github.com/Jonggil-dev/TIL/assets/155353613/c340f4c7-74b2-4c5a-95ec-9a0518634e7d)


### 📄 로그인 절차 상세 설명

**1.사용자가 아이디와 비밀번호를 입력하여 서버로 전송**

**2. UsernamePasswordAuthenticationToken(토큰, 인증용 객체) 생성**

- `UsernamePasswordAuthenticationFilter(이하 AuthenticationFilter)`가 요청을 받아서 `UsernamePasswordAuthenticationToken` (인증용 객체)을 생성
- `UsernamePasswordAuthenticationToken`는 `Authentication`타입의 객체임
- 이 시점의 `UsernamePasswordAuthenticationToken`은 인증되지 않은 상태(즉, `authenticated` 속성이 `false`)

**3. Authentication Manager에게 처리 위임**

- `AuthenticationFilter`는 `UsernamePasswordAuthenticationToken`을 `AuthenticationManager`에게 보내면서 인증을 위임
  ex) 아이디와 비번이 담긴 토큰을 보낼테니 이걸로 회원가입한 유저인지 확인해줘! (인증)

**4. Authentication Provider 선택**

- `AuthenticationManager`은 List 형태로 `AuthenticationProvider`를 가지고 있음
- `UsernamePasswordAuthenticationToken`을 처리할 수 있는 `AuthenticationProvider` 선택
- `AuthenticationProvider`에게 인증용 객체(토큰)를 다시 위임
- `AuthenticationManager`은 인증을 담당하는 클래스이지만, **실제 인증 로직을 수행하는 것은 `AuthenticationProvider` 인터페이스**

**5. 인증 절차**

- `AuthenticationProvider`는 `authenticate()`메서드를 통해  **DB에 있는 유저 정보**와 **UsernamePasswordAuthenticationToken(인증용 객체)에 담긴 유저 정보**를 비교

  - `authenticate()`는 `UsernamePasswordAuthenticationToken`을 매개변수로 받음

  - **로그인 request에 담긴 유저 정보** : `authenticate()`내 매개변수(`UsernamePasswordAuthenticationToken`) 에서 유저 정보 가져옴

  - **DB에 있는 유저 정보** : `authenticate()`내 `userDetailService`를 호출해 유저 정보 가져옴

    - `UserDetailsService`는 `loadUserByUsername(유저 아이디)` 메서드를 호출하여 DB에 유저 정보가 있을 경우 `UserDetails` 타입으로 생성.

    - 이 때 ` loadUserByUsername`의 매개변수는 `UsernamePasswordAuthenticationToken`에서 추출

    - ```java
      @Service
      @RequiredArgsConstructor
      public class CustomUserDetailsService implements UserDetailsService {
      
          private final UserRepository userRepository;
      
          @Override
          public UserDetails loadUserByUsername(String userLoginId) throws UsernameNotFoundException {
              return userRepository.findByUserLoginId(userLoginId)
                      .map
                              (this::createUserDetails)
                      .orElseThrow(() -> new UsernameNotFoundException("해당하는 회원을 찾을 수 없습니다."));
          }
      
          // 해당하는 User 의 데이터가 존재한다면 UserDetails 객체로 만들어서 return
          private UserDetails createUserDetails(com.e102.simcheonge_server.domain.user.entity.User user) {
              return User.builder()
                      .username(user.getUserLoginId())
                      .password(user.getUserPassword())
                      .roles("USER") //명시적으로 모든 유저에게 "기본 ROLE_USER" 권한 부여 (프로젝트에서 권한의 쓰임이 없기 떄문)
      //                .roles(user.getRoles().toArray(new String[0]))
                      .build();
          }
      }
      ```

**6. 인증 성공**

- 인증에 성공하면, `UsernamePasswordAuthenticationToken`을 인증된 상태(`authenticated` 속성이 `true`)로 업데이트 됨
- `AuthenticationProvider`에서 인증된 `UsernamePasswordAuthenticationToken`를 `Authentication 객체`에 담아 `AuthenticationManager`에게 전달
- `AuthenticationManager`은 `Authentication 객체`를 다시 `AuthenticationFilter`에게 전달한다.

**7. JWT 토큰 생성 및 발급 (하기 2가지 방법으로 나뉨)**

1. **AuthenticationSuccessHandler의 커스텀 구현**
   - **`AuthenticationSuccessHandler` 호출**
     -  `AuthenticationFilter`는 `Authentication 객체`를 받은 후 커스텀 `AuthenticationSuccessHandler(인증 성공 핸들러)`를 호출
     - 이 핸들러는 개발자가 구현해야 함.
   - **JWT 토큰 생성 및 응답**
     - `AuthenticationSuccessHandler`내에서, `Authentication` 객체에서 사용자의 정보를 추출하여 JWT 토큰을 생성
     - 생성된 토큰은 HTTP 응답(보통 헤더 또는 바디 내)에 포함시켜 클라이언트에게 전송

2. **JWT AuthenticationFilter를 통한 처리**
   - **커스텀 필터 구현**
     - 개발자는 `(UsernamePassword)AuthenticationFilter` 후에 실행될 `JWTAuthenticationFilter(커스텀 필터)`를 구현. 이 필터는 인증 과정을 직접 처리하지 않고, 대신 인증이 성공했을 때의 후속 조치를 담당
     - `JWTAuthenticationFilter`는 직접 전달 받은 `Authentication` 객체를 기반으로 JWT 토큰을 생성
     - 생성된 JWT 토큰은 응답에 포함되어 클라이언트에게 전송
   - **필터 체인에 JWT AuthenticationFilter 추가**
     - Spring Security 설정에서 이 커스텀 필터를 필터 체인에 명시적으로 추가합니다. 이 필터의 위치는 `(UsernamePassword)AuthenticationFilter` 이후이어야 함
   

### 📄 로그인 인증 이후의 요청 처리 과정 (!!!!!수정 필요!!!!!!)

**1. JWT 토큰의 전송**

- 클라이언트는 요청의 `Authorization` 헤더에 `Bearer {accesstoken}` 형식으로 포함시켜 서버에 전송

**2. JWT 토큰 검증 (커스텀 필터, JwtAuthenticationFilter)**

- 서버는 `OncePerRequestFilter` 를 상속받는 커스텀 필터 `JwtAuthenticationFilter `요청의 `Authorization 헤더`를 검사하여 JWT 토큰을 추출
- 이 필터는 요청이 들어올 때마다 실행되며, 토큰의 유효성을 검사

**3. SecurityContext 설정**

- 토큰이 유효하면, 필터는 토큰에 포함된 사용자 식별 정보를 기반으로 `Authentication` 객체(`UsernamePasswordAuthenticationToken` 또는 `JwtAuthenticationToken`)를 생성
- **SecurityContextHolder에 인증 객체 설정**: 생성된 `Authentication` 객체는 `SecurityContextHolder`의 `SecurityContext`에 저장됩니다. 이로써, 애플리케이션의 다른 부분에서 현재 인증된 사용자의 정보에 접근할 수 있게 됩니다.

4. 사용자 인증 여부 확인

- **인증 상태 접근**: 애플리케이션 내에서 현재 요청을 처리하는 컴포넌트(예: 컨트롤러, 서비스)는 `SecurityContextHolder`를 통해 인증된 사용자의 정보(`Authentication` 객체)에 접근할 수 있습니다. 이 정보를 바탕으로 사용자의 인증 상태를 확인할 수 있습니다.

### 5. 요청에 대한 접근 권한 검사

- **접근 권한 검사**: 인증된 사용자의 권한을 확인하여, 현재 요청이 접근하려는 리소스나 작업에 대한 접근 권한이 있는지 검사합니다. 이는 `@PreAuthorize`, `@Secured` 어노테이션, 메소드 시큐리티, URL 기반 시큐리티 등 다양한 방식으로 구현될 수 있습니다.

### 6. 요청 처리

- **리소스 접근**: 사용자가 요청한 리소스에 대한 접근 권한이 확인되면, 서버는 요청을 처리하고 필요한 데이터나 서비스를 제공합니다.
- **접근 거부 처리**: 사용자가 접근 권한이 없는 리소스에 대한 요청을 한 경우, Spring Security는 접근 거부 응답(예: HTTP 403 Forbidden)을 자동으로 처리하고 반환합니다.



### 🚡 객체별 역할 요약

- `(UsernamePassword)AuthenticationFilter`
  : 정식 명칭은 `UsernamePasswordAuthenticationFilter` .
  : 인증되지 않은 사용자와 인증된 사용자의 요청을 감시하고, `AuthenticationManager`에게 인증 처리를 맡김

  - 인증 성공 → 인증용 객체를 `AuthenticationContext`에 저장 후 `AuthenticationSuccessHandler` 실행
  - 인증 실패 → `AuthenticationFailureHandler` 실행

- `AuthenticationProvider`

  : `authenticate()` 메서드를 통해 실제 인증 로직을 실행 (로그인시 입력한 정보와 DB에 저장된 정보를 비교)

  : 인증 과정 중에 DB에 저장된 유저 정보가 필요할 때 `UserDetailsService`를 호출

  - 일치 → `Authentication 객체`에 담아 `AuthenticationManager`에게 전달
  - 불일치 → 예외 처리

- `UserDetailsService`
  : DB에서 유저 정보를 가져오는 역할을 담당

  - `loadUserByUsername()` 메서드를 통해 로그인 요청 사용자의 상세 정보를 담고 있는 `UserDetails` 객체를 반환.

- `UserDetails`
  : 사용자의 정보를 담는 인터페이스

  - 아래는 UserDetails를 상속받은 CustomUserDetails 사용시 필수로 구현 해야 하는 메서드
  ![userDetail 메서드](https://github.com/Jonggil-dev/TIL/assets/155353613/6f6059b8-30c0-41ae-974b-1241c7e32494)



