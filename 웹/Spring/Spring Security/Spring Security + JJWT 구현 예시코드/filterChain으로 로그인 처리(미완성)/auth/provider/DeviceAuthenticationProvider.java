package com.example.icecream.common.auth.provider;

import com.example.icecream.domain.user.dto.DeviceLoginRequestDto;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

public class DeviceAuthenticationProvider implements AuthenticationProvider {

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        DeviceLoginRequestDto dto = (DeviceLoginRequestDto) authentication.getPrincipal();

        // 여기에 디바이스 인증 로직 구현
        if ("특정 조건".equals(dto.getDeviceId())) {
            return new UsernamePasswordAuthenticationToken(dto.getDeviceId(), null, new ArrayList<>());
        }
        throw new AuthenticationException("디바이스 인증 실패") {};
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return DeviceLoginAuthenticationToken.class.isAssignableFrom(authentication);
    }
}