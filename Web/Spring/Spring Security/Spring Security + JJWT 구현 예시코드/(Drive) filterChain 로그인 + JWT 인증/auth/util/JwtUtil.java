package com.drive.sidepjt.domain.auth.util;

import com.drive.sidepjt.common.exception.InternalServerException;
import com.drive.sidepjt.common.exception.NotFoundException;
import com.drive.sidepjt.domain.auth.dto.JwtTokenDto;
import com.drive.sidepjt.domain.auth.dto.RefreshTokenDto;
import com.drive.sidepjt.domain.auth.error.AuthErrorCode;
import com.drive.sidepjt.domain.user.entity.User;
import com.drive.sidepjt.domain.user.error.UserErrorCode;
import com.drive.sidepjt.domain.user.repository.UserRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    private final UserRepository userRepository;
    private final RedisTemplate<String, String> redisTemplate;

    @Value("${com.drive.sidepjt.auth.access.secretKey}")
    private String accessSecretKey;
    @Value("${com.drive.sidepjt.auth.refresh.secretKey}")
    private String refreshSecretKey;

    public JwtTokenDto generateTokenByFilterChain(Authentication authentication, int userId) {
        byte[] accessSigningKey = accessSecretKey.getBytes();
        byte[] refreshSigningKey = refreshSecretKey.getBytes();

        String authority = authentication.getAuthorities().iterator().next().getAuthority();

        long now = (new Date()).getTime();

        Date accessTokenExpiresIn = new Date(now + 28800000);
        String accessToken = Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("authority", authority)
                .setExpiration(accessTokenExpiresIn)
                .signWith(Keys.hmacShaKeyFor(accessSigningKey), SignatureAlgorithm.HS256)
                .compact();


        Date refreshTokenExpiresIn = new Date(now + 43200000);
        String refreshToken = Jwts.builder()
                .claim("authority", authority)
                .setExpiration(refreshTokenExpiresIn)
                .signWith(Keys.hmacShaKeyFor(refreshSigningKey), SignatureAlgorithm.HS256)
                .compact();

        saveRefreshToken(String.valueOf(userId), refreshToken);

        return JwtTokenDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public JwtTokenDto generateToken(int userId) {
        byte[] accessSigningKey = accessSecretKey.getBytes();
        byte[] refreshSigningKey = refreshSecretKey.getBytes();


        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(UserErrorCode.USER_NOT_FOUND.getMessage()));

        long now = (new Date()).getTime();

        Date accessTokenExpiresIn = new Date(now + 28800000);
        String accessToken = Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("authority", "ROLE_" + user.getType())
                .setExpiration(accessTokenExpiresIn)
                .signWith(Keys.hmacShaKeyFor(accessSigningKey), SignatureAlgorithm.HS256)
                .compact();


        Date refreshTokenExpiresIn = new Date(now + 43200000);
        String refreshToken = Jwts.builder()
                .claim("authority", "ROLE_" + user.getType())
                .setExpiration(refreshTokenExpiresIn)
                .signWith(Keys.hmacShaKeyFor(refreshSigningKey), SignatureAlgorithm.HS256)
                .compact();

        saveRefreshToken(String.valueOf(userId), refreshToken);

        return JwtTokenDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }


    // Jwt 토큰을 복호화하여 토큰에 들어있는 정보를 꺼내고 Authentication 객체를 만들어 반환
    public Authentication getAuthentication(String accessToken) {

        Claims claims = parseAccessClaims(accessToken);
        String authorityString = claims.get("authority").toString();
        GrantedAuthority authorityGr = new SimpleGrantedAuthority(authorityString);
        Collection<GrantedAuthority> authority = Collections.singletonList(authorityGr);

        UserDetails principal = new org.springframework.security.core.userdetails.User(claims.getSubject(), "", authority);
        return new UsernamePasswordAuthenticationToken(principal, "", authority);
    }

    // 토큰 정보를 검증하는 메서드
    public boolean validateAccessToken(String token) {
        try {
            byte[] accessSigningKey = accessSecretKey.getBytes();

            Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(accessSigningKey))
                    .build()
                    .parseClaimsJws(token);

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean validateRefreshToken(String refreshToken, int userId) {
        String redisRefreshToken = findRefreshTokenInRedis(String.valueOf(userId));

        if (!redisRefreshToken.equals(refreshToken)) {
            throw new BadCredentialsException(AuthErrorCode.INVALID_TOKEN.getMessage());
        }
        parseRefreshClaims(refreshToken);
        return true;
    }

    public JwtTokenDto reissueToken(RefreshTokenDto refreshTokenDto) {
        try {
            if (validateRefreshToken(refreshTokenDto.getRefreshToken(), refreshTokenDto.getUserId())) {
                return generateToken(refreshTokenDto.getUserId());
            } else {
                throw new BadCredentialsException(AuthErrorCode.INVALID_TOKEN.getMessage());
            }
        } catch (Exception e) {
            throw new BadCredentialsException(AuthErrorCode.INVALID_TOKEN.getMessage());
        }
    }


    private Claims parseAccessClaims(String accessToken) {
        try {
            byte[] accessSigningKey = accessSecretKey.getBytes();

            return Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(accessSigningKey))
                    .build()
                    .parseClaimsJws(accessToken)
                    .getBody();
        } catch (Exception e) {
            throw new BadCredentialsException(AuthErrorCode.INVALID_TOKEN.getMessage());
        }
    }

    private void parseRefreshClaims(String accessToken) {
        try {
            byte[] refreshSigningKey = refreshSecretKey.getBytes();
            Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(refreshSigningKey))
                    .build()
                    .parseClaimsJws(accessToken)
                    .getBody();
        } catch (Exception e) {
            throw new BadCredentialsException(AuthErrorCode.INVALID_TOKEN.getMessage());
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

    public void invalidateRefreshToken(Integer userId) {
        try {
            redisTemplate.delete("refreshToken:" + userId);
        } catch (Exception e) {
            throw new InternalServerException(AuthErrorCode.FAILURE_DELETED_REFRESH.getMessage());
        }

    }
}