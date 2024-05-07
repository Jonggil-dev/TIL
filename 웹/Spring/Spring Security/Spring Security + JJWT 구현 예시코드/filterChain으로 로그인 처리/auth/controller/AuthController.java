package com.example.icecream.common.auth.controller;

import com.example.icecream.common.auth.dto.LoginResponseDto;
import com.example.icecream.common.auth.service.AuthService;
import com.example.icecream.common.auth.util.JwtUtil;
import com.example.icecream.common.dto.ApiResponseDto;
import com.example.icecream.common.auth.dto.DeviceLoginRequestDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    @PostMapping("/device/login")
    public ResponseEntity<ApiResponseDto<LoginResponseDto>> login(@RequestBody @Valid DeviceLoginRequestDto deviceLoginRequestDto)
    {
        return ApiResponseDto.success("로그인 되었습니다.", authService.deviceLogin(deviceLoginRequestDto));
    }

//    @PostMapping("/logout")
//    public ResponseEntity<?> signOut(@RequestBody LogoutRequest logoutRequest, @AuthenticationPrincipal UserDetails userDetails) {
//        User user = UserUtil.getUserFromUserDetails(userDetails);
//        jwtUtil.invalidateRefreshToken(user.getUserLoginId());
//        return ResponseUtil.buildBasicResponse(HttpStatus.OK,"로그아웃 되었습니다.");
//    }
//
//    @PostMapping("/reissue")
//    public ResponseEntity<?> reissue(@RequestBody JwtToken jwtToken) {
//        JwtToken newToken = jwtTokenProvider.reissueToken(jwtToken.getRefreshToken());
//        return ResponseUtil.buildBasicResponse(HttpStatus.OK, newToken);
//    }
}