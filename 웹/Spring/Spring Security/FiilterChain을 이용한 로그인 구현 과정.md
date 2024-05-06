# Spring Security의 FiilterChain을 이용한 Login 인증 구현 방법

- ### 흐름

  1. **`CustomUsernamePasswordAuthenticationFilter` 작성**
     - JSON 타입의 request 객체를 역직렬화 해서 login 인증 과정 수행하기 위함
       -  JSON 타입의 login_Id와 password를 LoginRequestDto 클래스로 매핑 (역직렬화)
     - 해당 필터는 설정한 url (ex) "/auth/login" 에서만 활성화 되도록 설정
     - 로그인 인증이 성공 했을 때 처리를 담당할 `customAuthenticationSuccessHandler` 설정

    2.  **`CustomUserDetailsService` 작성**

        - 로그인 인증을 수행할 때 `usernamePasswordAuthenticationToken`와 비교 대상이 되는 **유저 정보 객체를 생성**
          - 즉, 인증에 필요한 유저 정보를 어떤 DB Table에서 어떤 자료들을 들고올 껀지 정해짐

    3.  **`CustomAuthenticationSuccessHandler` 작성**

        - `CustomUsernamePasswordAuthenticationFilter`에서 인증이 성공 했을 때의 처리를 담당
        - 즉, 로그인 인증이 성공 하고 이후의 로직이나 응답을 커스텀 할 수 있는 곳

    4.  **`CustomUsernamePasswordAuthenticationFilter`에 `CustomUserDetailsService, CustomAuthenticationSuccessHandler`를 등록**

        - `CustomUserDetailsService`의 경우 `authenticationManager`에 `CustomUserDetailsService`를 등록하고 해당 `authenticationManager`를 `CustomUsernamePasswordAuthenticationFilter`에 등록하는 방식임

          - 더 자세하게는 `CustomUsernamePasswordAuthenticationFilter`의 부모 클래스인 `UsernamePasswordAuthenticationFilter`에 `authenticationManager`를 등록해서 `CustomUserDetailsService`를 사용함

        - `CustomAuthenticationSuccessHandler`의 경우 `CustomUsernamePasswordAuthenticationFilter`의 부모 클래스인 `UsernamePasswordAuthenticationFilter`에 등록하여 `CustomUsernamePasswordAuthenticationFilter`에서 사용함

          - `CustomUsernamePasswordAuthenticationFilter`에서 인증이 성공적으로 이루어질 경우 `successfulAuthentication()`메서드가 실행되는데 해당 메서드에서 `CustomAuthenticationSuccessHandler`를 호출하여 로그인 인증 이후의 커스텀 로직을 처리하는 방식임

          

- ### 각 흐름별 예시 코드