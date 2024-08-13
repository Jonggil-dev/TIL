package com.drive.sidepjt.domain.auth.controller;

import com.drive.sidepjt.common.dto.BasicResponse;
import com.drive.sidepjt.common.util.ResponseUtil;
import com.drive.sidepjt.domain.auth.dto.JwtTokenDto;
import com.drive.sidepjt.domain.auth.dto.RefreshTokenDto;
import com.drive.sidepjt.domain.auth.util.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final JwtUtil jwtUtil;

    @GetMapping("/logout")
    public ResponseEntity<BasicResponse<Void>> logout(@AuthenticationPrincipal UserDetails userDetails) {
        jwtUtil.invalidateRefreshToken(Integer.parseInt(userDetails.getUsername()));
        return ResponseUtil.buildBasicResponse(HttpStatus.OK,"로그아웃 되었습니다.", null);
    }

    @PostMapping("/reissue")
    public ResponseEntity<BasicResponse<JwtTokenDto>> reissue(@RequestBody @Valid final RefreshTokenDto refreshTokenDto) {
        JwtTokenDto newToken = jwtUtil.reissueToken(refreshTokenDto);
        return ResponseUtil.buildBasicResponse(HttpStatus.OK, "토큰이 재발급 되었습니다.", newToken);
    }
}