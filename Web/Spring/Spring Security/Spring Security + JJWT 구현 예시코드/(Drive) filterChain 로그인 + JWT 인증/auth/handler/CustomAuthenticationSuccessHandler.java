package com.drive.sidepjt.domain.auth.handler;

import com.drive.sidepjt.common.dto.BasicResponse;
import com.drive.sidepjt.domain.auth.dto.JwtTokenDto;
import com.drive.sidepjt.domain.auth.util.JwtUtil;
import com.drive.sidepjt.domain.user.entity.User;
import com.drive.sidepjt.domain.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final ObjectMapper objectMapper;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        User user = userRepository.findByLoginId(authentication.getName()).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저 입니다."));

        JwtTokenDto jwtTokenDto = jwtUtil.generateTokenByFilterChain(authentication, user.getId());

        String fcmToken = (String) request.getAttribute("fcmToken");

        if (fcmToken == null || !Pattern.compile("^[a-zA-Z0-9\\-_:]{100,}$").matcher(fcmToken).matches()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"error\": \"올바른 FCM 토큰 형식이 아닙니다.\"}");
            response.getWriter().flush();
            return;
        }

        userRepository.saveOrUpdateFcmToken(fcmToken, user);

        BasicResponse<JwtTokenDto> basicResponse = new BasicResponse<>(200, "로그인 되었습니다.", jwtTokenDto);
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(basicResponse));
        response.getWriter().flush();
    }
}