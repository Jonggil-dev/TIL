package com.e102.simcheonge_server.domain.user.controller;

import com.e102.simcheonge_server.common.util.ResponseUtil;
import com.e102.simcheonge_server.domain.user.dto.request.SignUpRequest;
import com.e102.simcheonge_server.domain.user.dto.request.UpdateNicknameRequest;
import com.e102.simcheonge_server.domain.user.dto.request.UpdatePasswordRequest;
import com.e102.simcheonge_server.domain.user.entity.User;
import com.e102.simcheonge_server.domain.user.service.UserService;
import com.e102.simcheonge_server.domain.user.utill.UserUtil;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import static com.e102.simcheonge_server.common.util.ResponseUtil.buildBasicResponse;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignUpRequest signUpRequestForm){
        log.info("signUpRequestForm={}",signUpRequestForm);
        userService.saveUser(signUpRequestForm);
        return buildBasicResponse(HttpStatus.OK,"회원 가입에 성공했습니다.");
    }

    // 인증된 사용자 정보 가져오는법 (쿠키-세션, JWT토큰 등 인증 방법과는 무관하게 사용가능)
    // 현재 SecurityContext에 저장된 Authentication 객체에서 주체를 추출하여 메소드의 파라미터로 주입할 수 있게 해주는 메서드
    // public ResponseEntity<?> example(@AuthenticationPrincipal UserDetails userDetails)
}

