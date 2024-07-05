package com.e102.simcheonge_server.domain.auth.controller;


import com.e102.simcheonge_server.common.util.ResponseUtil;
import com.e102.simcheonge_server.domain.auth.dto.JwtToken;
import com.e102.simcheonge_server.domain.auth.dto.request.LogoutRequest;
import com.e102.simcheonge_server.domain.auth.security.jwt.JwtTokenProvider;
import com.e102.simcheonge_server.domain.auth.security.jwt.JwtUtil;
import com.e102.simcheonge_server.domain.auth.service.AuthService;
import com.e102.simcheonge_server.domain.auth.dto.request.LoginRequest;
import com.e102.simcheonge_server.domain.user.entity.User;
import com.e102.simcheonge_server.domain.user.utill.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/login")

    public ResponseEntity<?> signIn(@RequestBody LoginRequest loginRequest)
    {
        return ResponseUtil.buildBasicResponse(HttpStatus.OK, authService.login(loginRequest));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> signOut(@RequestBody LogoutRequest logoutRequest, @AuthenticationPrincipal UserDetails userDetails) {
        User user = UserUtil.getUserFromUserDetails(userDetails);
        jwtUtil.invalidateRefreshToken(user.getUserLoginId());
        return ResponseUtil.buildBasicResponse(HttpStatus.OK,"로그아웃 되었습니다.");
    }

    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(@RequestBody JwtToken jwtToken) {
        JwtToken newToken = jwtTokenProvider.reissueToken(jwtToken.getRefreshToken());
        return ResponseUtil.buildBasicResponse(HttpStatus.OK, newToken);
    }
}