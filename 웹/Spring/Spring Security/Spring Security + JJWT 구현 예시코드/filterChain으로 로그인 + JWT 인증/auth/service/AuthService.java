package com.example.icecream.common.auth.service;

import com.example.icecream.common.auth.dto.*;
import com.example.icecream.common.auth.error.AuthErrorCode;
import com.example.icecream.common.auth.util.JwtUtil;
import com.example.icecream.common.exception.NotFoundException;
import com.example.icecream.domain.notification.dto.LoginRequestDto;
import com.example.icecream.domain.notification.service.NotificationService;
import com.example.icecream.domain.user.entity.User;
import com.example.icecream.domain.user.repository.ParentChildMappingRepository;
import com.example.icecream.domain.user.repository.UserRepository;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final ParentChildMappingRepository parentChildMappingRepository;
    private final JwtUtil jwtUtil;
    private final NotificationService notificationService;

    public LoginResponseDto deviceLogin(DeviceLoginRequestDto deviceLoginRequestDto) {

        User user = userRepository.findByDeviceId(deviceLoginRequestDto.getDeviceId())
                .orElseThrow(() -> new NotFoundException(AuthErrorCode.USER_NOT_FOUND.getMessage()));

        if (user.getIsParent()) {
            if (jwtUtil.validateToken(deviceLoginRequestDto.getRefreshToken())) {
                List<User> children = parentChildMappingRepository.findChildrenByParentId(user.getId());

                JwtTokenDto jwtTokenDto = jwtUtil.generateTokenByController(String.valueOf(user.getId()), "ROLE_PARENT");

                LoginRequestDto loginRequestDto = new LoginRequestDto(user.getId(), deviceLoginRequestDto.getFcmToken());
                notificationService.saveOrUpdateFcmToken(loginRequestDto);

                return ParentLoginResponseDto.builder()
                        .username(user.getUsername())
                        .loginId(user.getLoginId())
                        .phoneNumber(user.getPhoneNumber())
                        .profileImage(user.getProfileImage())
                        .children(children)
                        .accessToken(jwtTokenDto.getAccessToken())
                        .refreshToken(jwtTokenDto.getRefreshToken())
                        .build();
            }
        }

        JwtTokenDto jwtTokenDto = jwtUtil.generateTokenByController(String.valueOf(user.getId()), "ROLE_CHILD");
        LoginRequestDto loginRequestDto = new LoginRequestDto(user.getId(), deviceLoginRequestDto.getFcmToken());
        notificationService.saveOrUpdateFcmToken(loginRequestDto);

        return ChildLoginResponseDto.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .phoneNumber(user.getPhoneNumber())
                .profileImage(user.getProfileImage())
                .accessToken(jwtTokenDto.getAccessToken())
                .refreshToken(jwtTokenDto.getRefreshToken())
                .build();
    }
}