package com.e102.simcheonge_server.domain.auth.service;

import com.e102.simcheonge_server.domain.auth.dto.JwtToken;
import com.e102.simcheonge_server.domain.auth.dto.request.LoginRequest;
import com.e102.simcheonge_server.domain.auth.dto.response.LoginResponse;
import com.e102.simcheonge_server.domain.auth.security.jwt.JwtTokenProvider;
import com.e102.simcheonge_server.domain.auth.security.jwt.JwtUtil;
import com.e102.simcheonge_server.domain.user.entity.User;
import com.e102.simcheonge_server.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;


import java.util.Optional;

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