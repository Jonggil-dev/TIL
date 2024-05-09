package com.example.icecream.common.auth.filter;

import com.example.icecream.common.auth.error.AuthErrorCode;
import com.example.icecream.common.auth.util.JwtUtil;
import com.example.icecream.common.dto.ApiResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean {

    private final JwtUtil jwtutil;
    private final ObjectMapper objectMapper;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        //permitAll() 요청 url에 대해서는 해당 필터의 동작 없이 다음 필터로 요청을 넘기기 위한 로직
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String requestURI = httpRequest.getRequestURI();
        List<String> skipUrls = List.of("/api/users/check", "/api/auth/login", "/api/auth/device/login", "/api/auth/reissue");
        boolean skip = skipUrls.stream().anyMatch(requestURI::equals);
        if ("/api/users".equals(requestURI) && "POST".equalsIgnoreCase(httpRequest.getMethod())) {
            skip = true;
        }
        if (skip) {
            chain.doFilter(request, response);
            return;
        }

        //access_token 검증
        String token = resolveToken((HttpServletRequest) request);
        try {
            if (token != null && jwtutil.validateToken(token)) {
                Authentication authentication = jwtutil.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            httpResponse.setContentType("application/json; charset=UTF-8");
            ApiResponseDto<String> apiResponse = new ApiResponseDto<>(HttpServletResponse.SC_UNAUTHORIZED, AuthErrorCode.INVALID_TOKEN.getMessage(), null);
            httpResponse.getWriter().write(objectMapper.writeValueAsString(apiResponse));
            return;
        }

        chain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer")) {
            return bearerToken.substring(7);
        }
        return null;
    }

}