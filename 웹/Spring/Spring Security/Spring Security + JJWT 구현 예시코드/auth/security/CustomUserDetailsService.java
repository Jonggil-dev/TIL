package com.e102.simcheonge_server.domain.auth.security;
import com.e102.simcheonge_server.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String userLoginId) throws UsernameNotFoundException {
        return userRepository.findByUserLoginId(userLoginId)
                .map
                        (this::createUserDetails)
                .orElseThrow(() -> new UsernameNotFoundException("해당하는 회원을 찾을 수 없습니다."));
    }

    // 해당하는 User 의 데이터가 존재한다면 UserDetails 객체로 만들어서 return
    private UserDetails createUserDetails(com.e102.simcheonge_server.domain.user.entity.User user) {
        return User.builder()
                .username(user.getUserLoginId())
                .password(user.getUserPassword())
                .roles("USER") //명시적으로 모든 유저에게 "기본 ROLE_USER" 권한 부여 (프로젝트에서 권한의 쓰임이 없기 떄문)
//                .roles(user.getRoles().toArray(new String[0]))
                .build();
    }

}
