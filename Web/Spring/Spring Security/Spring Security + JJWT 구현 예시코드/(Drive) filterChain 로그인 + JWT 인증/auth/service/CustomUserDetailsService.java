package com.drive.sidepjt.domain.auth.service;

import com.drive.sidepjt.common.exception.NotFoundException;
import com.drive.sidepjt.domain.auth.error.AuthErrorCode;
import com.drive.sidepjt.domain.user.entity.User;
import com.drive.sidepjt.domain.user.repository.UserRepository;

import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {

        return userRepository.findByLoginId(loginId)
                .map(this::createUserDetails)
                .orElseThrow(() -> new AuthenticationServiceException(AuthErrorCode.USER_NOT_FOUND.getMessage()));
    }

    private UserDetails createUserDetails(User user) {
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getLoginId())
                .password(user.getPassword())
                .roles(user.getType())
                .build();
    }
}
