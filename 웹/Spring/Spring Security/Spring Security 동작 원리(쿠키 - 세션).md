# Spring Security 동작 원리(쿠키 - 세션)



## 출처 

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
![전체 동작 원리](https://github.com/Jonggil-dev/TIL/assets/155353613/8fc7b262-f51d-4e6e-93cc-a8f9d16abaeb)


### 📄 로그인 절차 상세 설명

**1.사용자가 아이디와 비밀번호를 입력하여 서버로 전송**

**2. UsernamePasswordAuthenticationToken(토큰, 인증용 객체) 생성**

- `AuthenticationFilter`가 요청을 받아서 `UsernamePasswordAuthenticationToken` (인증용 객체)을 생성
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
-  `AuthenticationProvider`에서 인증된 `UsernamePasswordAuthenticationToken`를 `Authentication 객체`에 담아 `AuthenticationManager`에게 전달
- `AuthenticationManager`은 `Authentication 객체`를 다시 `AuthenticationFilter`에게 전달한다.

**7.AuthenticatioinSuccessHandler 실행**

- `AuthenticationFilter`는 `Authentication 객체`를 `SecurityContextHolder`의 `SecurityContext` 라는 곳에 저장하고 `AuthenticationSuccessHandler`을 실행
  - 실패 시 `AuthenticationFailureHandler` 실행
  - 이후 인증된 사용자가 다시 요청을 보내게 되면, `SecurityContext` 객체 안에 `Authentication`이 있는지 확인 후 있다면 바로 인과처리로 넘어감으로 인해 효율적 처리가 가능

**8. 세션 생성 및 세션 ID 발급 실행**

- 인증이 성공하면, 서버는 사용자에 대한 세션을 생성하고, 이 세션에 `SecurityContext`를 저장. 그리고 생성된 세션 ID를 클라이언트에게 쿠키로 전송



### 📄 로그인 인증 이후의 요청 처리 과정

**1. 세션 ID의 전송**

- 클라이언트는 요청을 보낼 때 HTTP 쿠키에 저장된 세션 ID를 함께 서버에 전송

**2. 세션 ID 검증**

- `SessionManagementFilter`는 해당 세션 ID를 추출하고 세션 저장소(메모리, DB, 외부 세션 관리 시스템 등)에서 사용자의 세션을 조회
- 세션을 성공적으로 조회하면, 해당 세션에 저장된 `SecurityContext`를 가져옴
- 이 `SecurityContext`는 앞서 로그인 성공 시 저장된 것으로, `Authentication` 객체를 포함

**3. SecurityContextHolder 설정**

- 가져온 `SecurityContext`는 현재 요청을 처리하는 동안 `SecurityContextHolder`에 설정
- 이를 통해, 현재 요청이 실행되는 동안 어디서든 `Authentication` 객체에 접근할 수 있음

**4. 사용자 인증 여부 확인**

- 서버는 세션 저장소에서 가져온 `SecurityContext`의  `Authentication 객체`를 기반으로 현재 요청이 인증된 사용자에 의해 이루어졌는지 확인함
- 이 과정에서 추가적인 로그인 절차 없이도 사용자의 인증 상태를 확인할 수 있음

**5. 요청에 대한 접근 권한 검사**

- 사용자의 인증이 되면, `SecurityContext`에 저장된 인증 정보(`Authentication` 객체)를 기반으로 현재 요청이 접근하려는 리소스에 대한 접근 권한이 있는지 확인
- 이 과정에서 `@PreAuthorize`, `@Secured` 어노테이션 또는 XML 기반의 접근 정책 등을 사용하여 세부적인 접근 제어를 설정할 수 있음

**6. 요청 처리**

- 접근 권한이 확인되면, 실제 요청을 처리
- 만약 사용자가 요구하는 리소스에 대한 접근 권한이 없는 경우, Spring Security는 접근 거부 응답(예: HTTP 403 Forbidden)을 클라이언트에게 전송

### 🚡 객체별 역할 요약

- `(UsernamePassword)AuthenticationFilter`
: 정식 명칭은 `UsernamePasswordAuthenticationFilter`
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
  ![userDetail 메서드](https://github.com/Jonggil-dev/TIL/assets/155353613/1cca7b89-a4c2-43f0-9e6a-06a2efc2569f)



