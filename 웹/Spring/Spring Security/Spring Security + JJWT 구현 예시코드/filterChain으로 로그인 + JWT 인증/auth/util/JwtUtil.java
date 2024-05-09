package com.example.icecream.common.auth.util;

import com.example.icecream.common.auth.dto.JwtTokenDto;
import com.example.icecream.common.auth.error.AuthErrorCode;
import com.example.icecream.common.exception.NotFoundException;
import com.example.icecream.domain.user.repository.UserRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.ObjectUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Component
@RequiredArgsConstructor
@Slf4j
public class JwtUtil {

    @Value("${com.icecream.auth.access.secretKey}")
    private String secretKey;
    private final RedisTemplate<String, String> redisTemplate;
    private final UserRepository userRepository;

    public JwtTokenDto generateTokenByFilterChain(Authentication authentication, int userId) {
        byte[] signingKey = secretKey.getBytes();

        String authority = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = (new Date()).getTime();

        Date accessTokenExpiresIn = new Date(now + 28800000);
        String accessToken = Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("authority", authority)
                .setExpiration(accessTokenExpiresIn)
                .signWith(Keys.hmacShaKeyFor(signingKey), SignatureAlgorithm.HS256)
                .compact();


        Date refreshTokenExpiresIn = new Date(now + 43200000);
        String refreshToken = Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setExpiration(refreshTokenExpiresIn)
                .claim("authority", authority)
                .signWith(Keys.hmacShaKeyFor(signingKey), SignatureAlgorithm.HS256)
                .compact();

        saveRefreshToken(String.valueOf(userId), refreshToken);

        return JwtTokenDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public JwtTokenDto generateTokenByController(String userId, String authority) {
        byte[] signingKey = secretKey.getBytes();

        long now = (new Date()).getTime();

        Date accessTokenExpiresIn = new Date(now + 28800000);
        String accessToken = Jwts.builder()
                .setSubject(userId)
                .claim("authority", authority)
                .setExpiration(accessTokenExpiresIn)
                .signWith(Keys.hmacShaKeyFor(signingKey), SignatureAlgorithm.HS256)
                .compact();


        Date refreshTokenExpiresIn = new Date(now + 43200000);
        String refreshToken = Jwts.builder()
                .setSubject(userId)
                .setExpiration(refreshTokenExpiresIn)
                .claim("authority", authority)
                .signWith(Keys.hmacShaKeyFor(signingKey), SignatureAlgorithm.HS256)
                .compact();

        saveRefreshToken(userId, refreshToken);

        return JwtTokenDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    // Jwt 토큰을 복호화하여 토큰에 들어있는 정보를 꺼내는 메서드
    public Authentication getAuthentication(String accessToken) {

        Claims claims = parseClaims(accessToken);

        if (claims.get("authority") == null) {
            throw new BadCredentialsException(AuthErrorCode.INVALID_TOKEN.getMessage());
        }

        Collection<? extends GrantedAuthority> authorities = Arrays.stream(claims.get("authority").toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        UserDetails principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    // 토큰 정보를 검증하는 메서드
    public boolean validateToken(String token) {
        try {
            byte[] signingKey = secretKey.getBytes();

            Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(signingKey))
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            throw new BadCredentialsException(AuthErrorCode.INVALID_TOKEN.getMessage());
        }
    }

    public JwtTokenDto reissueToken(String refreshToken) {
        validateToken(refreshToken);

        Claims claims = parseClaims(refreshToken);
        String userId = claims.getSubject();
        String authority = claims.get("authority", String.class);

        com.example.icecream.domain.user.entity.User user = userRepository.findById(Integer.parseInt(userId))
                .orElseThrow(() -> new NotFoundException(AuthErrorCode.USER_NOT_FOUND.getMessage()));

        String redisRefreshToken = findRefreshTokenInRedis(userId);

        if (ObjectUtils.isEmpty(redisRefreshToken)) {
            throw new BadCredentialsException(AuthErrorCode.NO_TOKEN_IN_REDIS.getMessage());
        }
        if (!redisRefreshToken.equals(refreshToken)) {
            throw new BadCredentialsException(AuthErrorCode.NO_TOKEN_IN_REDIS.getMessage());
        }

        return generateTokenByController(userId,authority);
    }


    private Claims parseClaims(String accessToken) {
        try {
            byte[] signingKey = secretKey.getBytes();
            return Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(signingKey))
                    .build()
                    .parseClaimsJws(accessToken)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    public void saveRefreshToken(String userId, String refreshToken) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        valueOperations.set("refreshToken:" + userId, refreshToken, 12, TimeUnit.HOURS);
    }

    public String findRefreshTokenInRedis(String userId) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        return valueOperations.get("refreshToken:" + userId);
    }

    public void invalidateRefreshToken(String userId) {
        redisTemplate.delete("refreshToken:" + userId);
    }
}