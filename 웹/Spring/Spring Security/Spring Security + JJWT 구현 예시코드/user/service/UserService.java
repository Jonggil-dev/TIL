package com.e102.simcheonge_server.domain.user.service;

import com.e102.simcheonge_server.common.exception.DataNotFoundException;
import com.e102.simcheonge_server.domain.user.dto.request.SignUpRequest;
import com.e102.simcheonge_server.domain.user.dto.request.UpdatePasswordRequest;
import com.e102.simcheonge_server.domain.user.entity.User;
import com.e102.simcheonge_server.domain.user.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void saveUser(final SignUpRequest signUpRequest){
        isValidateLoginId(signUpRequest.getUserLoginId());
        isValidateNickname(signUpRequest.getUserNickname());
        isValidatePassword(signUpRequest.getUserPassword(), signUpRequest.getUserPasswordCheck());


        String hashedPassword = passwordEncoder.encode(signUpRequest.getUserPassword());

        User user=User.builder()
                .userLoginId(signUpRequest.getUserLoginId())
                .userPassword(hashedPassword)
                .userNickname(signUpRequest.getUserNickname())
                .build();
        userRepository.save(user);
    }
}
