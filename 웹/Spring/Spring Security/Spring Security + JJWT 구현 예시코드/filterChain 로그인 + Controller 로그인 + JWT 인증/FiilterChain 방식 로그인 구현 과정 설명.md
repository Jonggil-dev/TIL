# Spring Security의 FiilterChain을 이용한 로그인 인증 구현 방법

- ### 흐름

  1. **`CustomUsernamePasswordAuthenticationFilter` 작성**
     - JSON 타입의 request 객체를 역직렬화 해서 login 인증 과정 수행하기 위함
       -  JSON 타입의 login_Id와 password를 LoginRequestDto 클래스로 매핑 (역직렬화)
     - 해당 필터는 설정한 url (ex) "/auth/login" 에서만 활성화 되도록 설정
     - 로그인 인증이 성공 했을 때 처리를 담당할 `customAuthenticationSuccessHandler` 설정
  2. **`CustomUserDetailsService` 작성**
     - 로그인 인증을 수행할 때 `usernamePasswordAuthenticationToken`와 비교 대상이 되는 **유저 정보 객체를 생성**
       - 즉, 인증에 필요한 유저 정보를 어떤 DB Table에서 어떤 자료들을 들고올 껀지 정해짐
  3. **`CustomAuthenticationSuccessHandler` 작성**
     - `CustomUsernamePasswordAuthenticationFilter`에서 인증이 성공 했을 때의 처리를 담당
     - 즉, 로그인 인증이 성공 하고 이후의 로직이나 응답을 커스텀 할 수 있는 곳
  4. **`CustomUsernamePasswordAuthenticationFilter`에 `CustomUserDetailsService, CustomAuthenticationSuccessHandler`를 등록**
     - `CustomUserDetailsService`의 경우 `authenticationManager`에 `CustomUserDetailsService`를 등록하고 해당 `authenticationManager`를 `CustomUsernamePasswordAuthenticationFilter`에 등록하는 방식임

       - 더 자세하게는 `CustomUsernamePasswordAuthenticationFilter`의 부모 클래스인 `UsernamePasswordAuthenticationFilter`에 `authenticationManager`를 등록해서 `CustomUserDetailsService`를 사용함

     - `CustomAuthenticationSuccessHandler`의 경우 `CustomUsernamePasswordAuthenticationFilter`의 부모 클래스인 `UsernamePasswordAuthenticationFilter`에 등록하여 `CustomUsernamePasswordAuthenticationFilter`에서 사용함
       - `CustomUsernamePasswordAuthenticationFilter`에서 인증이 성공적으로 이루어질 경우 `successfulAuthentication()`메서드가 실행되는데 해당 메서드에서 `CustomAuthenticationSuccessHandler`를 호출하여 로그인 인증 이후의 커스텀 로직을 처리하는 방식임

  5. 참고
     - **실제 인증을 진행하는 방법을 변경하고 싶으면(ex. `request`의 `deviceId`와 DB에 등록된 유저의 `deviceId`를 비교해서 인증 수행 등) `AuthenticationProvider`를 커스텀 해서 `AuthenticationManger`에 등록해야됨**
     - 4번 까지가 로그인 인증을 구성하는 방법이고, 이후 Jwt 토큰을 검증하는 로직을 구성하고 싶으면 `JwtAuthenticatinFilter`를 생성하고 `filterChain`에 등록 하면 됨

  

- ### 각 흐름별 예시 코드

  - **`LoginIdAuthenticationFilter(=CustomUsernamePasswordAuthenticationFilter)`**

    - 하기 예시코드의 생성자 설명
      - `CustomUserDetails`를 사용하기로 세팅 되어 있는 `AuthenticationManager`를 사용하도록 설정함 (이 때 `authenticationManager`에 `CustomUserDetails`를 설정하는 것은 `SecurityConfig`에서 설정 해놓음)
      - `CustomAuthenticationSuccessHandler`를 사용하겠다고 설정
      - `/auth/login` Url에 대해서만 해당 필터를 활성화 하겠다는 설정
    - `attemptAuthentication()`
      - `AuthenticationFilter`에서 인증 과정을 수행하는 메서드
      - 실제 인증은  `AuthenticationManager`의 `authenticate()` 메서드를 통해서 인증을 수행할`AuthenticationProvider`가 선택되고 `AuthenticationProvider`에서 실제 인증 로직이 진행됨
        - 인증 로직은 `request`에 담긴 정보로 만들어진 `usernamePasswordAuthenticationToken`과 `UserDetailService`에서 DB에 저장된 유저 정보를 바탕으로 생성한 `UserDetails`객체를 비교하여 인증 과정을 수행함
        - 기본적으로 `UsernamePasswordAuthenticationFilter`에 설정된 `DaoAuthenticationProvider`의 인증 로직은 `usernamePasswordAuthenticationToken`의 `credentials` 속성과 `UserDetails`객체의 `password `속성을 비교하는 로직임
    - 인증이 성공하면 `successfulAuthentication()` 메서드를 호출하여 `AuthenticationSuccessHandler`에서 인증 성공 후의 로직을 처리함

    ```java
    package com.example.icecream.domain.user.auth.filter;
    
    import com.example.icecream.domain.user.auth.dto.LoginRequestDto;
    import com.example.icecream.domain.user.auth.error.AuthErrorCode;
    import com.example.icecream.domain.user.auth.handler.CustomAuthenticationSuccessHandler;
    import com.example.icecream.common.exception.BadRequestException;
    import com.example.icecream.common.exception.InternalServerException;
    import com.fasterxml.jackson.databind.ObjectMapper;
    import jakarta.servlet.FilterChain;
    import jakarta.servlet.ServletException;
    
    import org.springframework.security.authentication.AuthenticationManager;
    import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
    import org.springframework.security.core.Authentication;
    import org.springframework.security.core.AuthenticationException;
    import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
    
    import jakarta.servlet.http.HttpServletRequest;
    import jakarta.servlet.http.HttpServletResponse;
    
    import org.springframework.stereotype.Component;
    
    import java.io.IOException;
    
    @Component
    public class LoginIdAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    
        private final ObjectMapper objectMapper;
    
        public LoginIdAuthenticationFilter(AuthenticationManager authenticationManager, CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler, ObjectMapper objectMapper) {
            super(authenticationManager);
            super.setAuthenticationSuccessHandler(customAuthenticationSuccessHandler);
            this.objectMapper = objectMapper;
            setFilterProcessesUrl("/auth/login");
        }
    
        @Override
        public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
            if (!request.getMethod().equals("POST")) {
                throw new BadRequestException(AuthErrorCode.INVALID_HTTP_METHOD.getMessage());
            }
    
            try {
    
                LoginRequestDto loginRequestDto = objectMapper.readValue(request.getInputStream(), LoginRequestDto.class);
                request.setAttribute("fcmToken", loginRequestDto.getFcmToken());
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                        new UsernamePasswordAuthenticationToken(loginRequestDto.getLoginId(),loginRequestDto.getPassword());
                return  getAuthenticationManager().authenticate(usernamePasswordAuthenticationToken);
    
            } catch (IOException ex) {
                throw new InternalServerException(AuthErrorCode.INPUT_SERIALIZE_FAIL.getMessage());
            }
        }
    
        @Override
        protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
            getSuccessHandler().onAuthenticationSuccess(request, response, authResult);
        }
    }
    ```
  
  - **`CustomUserDetailsService`**
  
    - 인증 과정에서 `request` 데이터와 비교가 되는 `UserDetails`의 객체를 생성하는 로직을 수행
      (DB에 저장된 유저 정보를 바탕으로 `UserDetails` 객체를 생성)
  
    ```java
    package com.example.icecream.domain.user.auth.service;
    
    import com.example.icecream.domain.user.auth.error.AuthErrorCode;
    import com.example.icecream.common.exception.NotFoundException;
    import com.example.icecream.domain.user.entity.User;
    import com.example.icecream.domain.user.repository.UserRepository;
    import lombok.RequiredArgsConstructor;
    import org.springframework.security.core.GrantedAuthority;
    import org.springframework.security.core.authority.SimpleGrantedAuthority;
    import org.springframework.security.core.userdetails.UserDetails;
    import org.springframework.security.core.userdetails.UserDetailsService;
    import org.springframework.security.core.userdetails.UsernameNotFoundException;
    import org.springframework.stereotype.Service;
    
    import java.util.Collection;
    import java.util.Collections;
    
    @Service
    @RequiredArgsConstructor
    public class CustomUserDetailsService implements UserDetailsService {
    
        private final UserRepository userRepository;
    
        @Override
        public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {
            return userRepository.findByLoginId(loginId)
                    .map(this::createUserDetails)
                    .orElseThrow(() -> new NotFoundException(AuthErrorCode.USER_NOT_FOUND.getMessage()));
        }
    
        private UserDetails createUserDetails(User user) {
            return org.springframework.security.core.userdetails.User.builder()
                    .username(user.getLoginId())
                    .password(user.getPassword())
                    .authorities(getAuthorities(user))
                    .build();
        }
    
        private Collection<? extends GrantedAuthority> getAuthorities(User user) {
            String role = user.getIsParent() ? "ROLE_PARENT" : "ROLE_CHILD";
            return Collections.singletonList(new SimpleGrantedAuthority(role));
        }
    }
    
    ```
  
  - **`CustomAuthenticationSuccessHandler`**
  
    - `onAuthenticationSuccess()`를 실행하여 인증 성공 후의 로직을 수행함
  
    ```java
    package com.example.icecream.domain.user.auth.handler;
    
    import com.example.icecream.domain.user.auth.dto.ChildrenResponseDto;
    import com.example.icecream.domain.user.auth.dto.JwtTokenDto;
    import com.example.icecream.domain.user.auth.dto.ParentLoginResponseDto;
    import com.example.icecream.domain.user.auth.util.JwtUtil;
    import com.example.icecream.common.dto.ApiResponseDto;
    import com.example.icecream.domain.notification.dto.LoginRequestDto;
    import com.example.icecream.domain.notification.service.NotificationService;
    import com.example.icecream.domain.user.entity.User;
    import com.example.icecream.domain.user.repository.ParentChildMappingRepository;
    import com.example.icecream.domain.user.repository.UserRepository;
    import com.fasterxml.jackson.databind.ObjectMapper;
    
    import lombok.RequiredArgsConstructor;
    import org.springframework.security.core.Authentication;
    import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
    import jakarta.servlet.http.HttpServletRequest;
    import jakarta.servlet.http.HttpServletResponse;
    import org.springframework.stereotype.Component;
    
    import java.io.IOException;
    import java.util.List;
    import java.util.regex.Pattern;
    
    @Component
    @RequiredArgsConstructor
    public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    
        private final ObjectMapper objectMapper;
        private final JwtUtil jwtUtil;
        private final UserRepository userRepository;
        private final ParentChildMappingRepository parentChildMappingRepository;
        private final NotificationService notificationService;
    
        @Override
        public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                            Authentication authentication) throws IOException {
    
            User user = userRepository.findByLoginId(authentication.getName()).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저 입니다."));
            List<User> children = parentChildMappingRepository.findChildrenByParentId(user.getId());
    
            List<ChildrenResponseDto> childrenResponseDto = children.stream()
                    .map(child -> new ChildrenResponseDto(child.getId(), child.getProfileImage(), child.getUsername(), child.getPhoneNumber()))
                    .toList();
    
            JwtTokenDto jwtToken = jwtUtil.generateTokenByFilterChain(authentication, user.getId());
    
            String fcmToken = (String) request.getAttribute("fcmToken");
    
            if (fcmToken == null || !Pattern.compile("^[a-zA-Z0-9\\-_:]{100,}$").matcher(fcmToken).matches()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"error\": \"올바른 FCM 토큰 형식이 아닙니다.\"}");
                response.getWriter().flush();
                return;
            }
    
            LoginRequestDto loginRequestDto = new LoginRequestDto(user.getId(), fcmToken);
            notificationService.saveOrUpdateFcmToken(loginRequestDto);
    
            ParentLoginResponseDto parentLoginResponseDto = ParentLoginResponseDto.builder()
                    .username(user.getUsername())
                    .loginId(user.getLoginId())
                    .phoneNumber(user.getPhoneNumber())
                    .profileImage(user.getProfileImage())
                    .children(childrenResponseDto)
                    .accessToken(jwtToken.getAccessToken())
                    .refreshToken(jwtToken.getRefreshToken())
                    .build();
    
            ApiResponseDto<ParentLoginResponseDto> apiResponse = new ApiResponseDto<>(200, "로그인 되었습니다.", parentLoginResponseDto);
    
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
            response.getWriter().flush();
        }
    }
    ```
  
  - **`SecurityConfig`**
  
    - `filterChain`에 커스텀한 필터들을 등록 
    - `CustomUserDetailsService`을 사용하도록 `authenticationManager`에 `CustomUserDetailsService`을 설정
    - 로그인 인증 과정에서 DB에 저장된 password 해싱 값과 비교하기 위해 request의 `password`를 해싱하는 과정이 필요함. 이 때 필요한 `passwordEncoder` 설정
  
    ```java
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