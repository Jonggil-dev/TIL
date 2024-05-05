package com.example.icecream.common.auth.filter;

import com.example.icecream.common.auth.token.DeviceAuthenticationToken;
import com.example.icecream.domain.user.dto.DeviceLoginRequestDto;

import com.example.icecream.domain.user.dto.LoginRequestDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.stereotype.Component;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;


@Component
public class CustomUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final ObjectMapper objectMapper;
    private final AuthenticationManager authenticationManager;

    public CustomUsernamePasswordAuthenticationFilter(ObjectMapper objectMapper, AuthenticationManager authenticationManager) {
        this.objectMapper = objectMapper;
        this.authenticationManager = authenticationManager;

        setRequiresAuthenticationRequestMatcher(
                new OrRequestMatcher(
                        new AntPathRequestMatcher("/device/login", "POST"),
                        new AntPathRequestMatcher("/login", "POST")
                )
        );
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            if (request.getRequestURI().endsWith("/device/login")) {
                DeviceLoginRequestDto deviceLoginDto = objectMapper.readValue(request.getInputStream(), DeviceLoginRequestDto.class);
                Authentication authRequest = new DeviceAuthenticationToken(deviceLoginDto.getDeviceId());
                return getAuthenticationManager().authenticate(authRequest);
            } else if (request.getRequestURI().endsWith("/id/login")) {
                LoginRequestDto loginRequestDto = objectMapper.readValue(request.getInputStream(), LoginRequestDto.class);
                Authentication authRequest = new UsernamePasswordAuthenticationToken(loginRequestDto.getLoginId(), loginRequestDto.getPassword());
                return getAuthenticationManager().authenticate(authRequest);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse login request", e);
        }
        return null;
    }
}