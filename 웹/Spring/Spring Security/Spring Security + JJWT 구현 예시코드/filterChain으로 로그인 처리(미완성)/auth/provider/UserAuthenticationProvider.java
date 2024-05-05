package com.example.icecream.common.auth.provider;

import com.example.icecream.domain.user.dto.LoginRequestDto;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

public class UserAuthenticationProvider implements AuthenticationProvider {

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        LoginRequestDto dto = (LoginRequestDto) authentication.getPrincipal();

        // 여기에 사용자 인증 로직 구현
        if ("유효한 사용자".equals(dto.getUsername())) {
            return new UsernamePasswordAuthenticationToken(dto.getUsername(), null, new ArrayList<>());
        }
        throw new AuthenticationException("사용자 인증 실패") {};
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
