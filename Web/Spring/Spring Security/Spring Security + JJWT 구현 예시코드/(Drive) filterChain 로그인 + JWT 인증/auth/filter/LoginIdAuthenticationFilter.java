package com.drive.sidepjt.domain.auth.filter;

import com.drive.sidepjt.common.exception.BadRequestException;
import com.drive.sidepjt.domain.auth.dto.LoginRequestDto;
import com.drive.sidepjt.domain.auth.handler.CustomAuthenticationSuccessHandler;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;


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
            //needModify(ErrorCode)
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }

        try {
            LoginRequestDto loginRequestDto = objectMapper.readValue(request.getInputStream(), LoginRequestDto.class);
            request.setAttribute("fcmToken", loginRequestDto.getFcmToken());
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                    new UsernamePasswordAuthenticationToken(loginRequestDto.getLoginId(),loginRequestDto.getPassword());

            //인증 시도
            Authentication authentication = getAuthenticationManager().authenticate(usernamePasswordAuthenticationToken);

            // 인증 성공 후 UserDetails 가져오기
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String userRole = userDetails.getAuthorities().iterator().next().getAuthority();

            // type 비교
            if (!userRole.equals("ROLE_" + loginRequestDto.getType())) {

                //needModify(ErrorCode)
                throw new AuthenticationServiceException("User type does not match");
            }

            return authentication;


        } catch (Exception ex) {

            //needModify(ErrorCode)
            throw new AuthenticationServiceException("Authentication request failed", ex);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        getSuccessHandler().onAuthenticationSuccess(request, response, authResult);
    }
}