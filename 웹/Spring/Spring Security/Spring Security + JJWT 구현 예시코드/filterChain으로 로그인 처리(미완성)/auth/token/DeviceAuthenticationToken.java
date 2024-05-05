package com.example.icecream.common.auth.token;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import java.util.Collection;

public class DeviceAuthenticationToken extends AbstractAuthenticationToken {
    private final Object principal;

    public DeviceAuthenticationToken(Object deviceId) {
        super(null);
        this.principal = deviceId;
        setAuthenticated(false);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    @Override
    public void eraseCredentials() {
    }
}
