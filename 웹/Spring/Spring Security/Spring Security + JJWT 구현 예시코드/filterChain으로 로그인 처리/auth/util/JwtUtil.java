package com.example.icecream.common.auth.util;

import com.example.icecream.common.auth.dto.JwtTokenDto;
import com.example.icecream.domain.user.repository.UserRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;


import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Component
@RequiredArgsConstructor
@Slf4j
public class JwtUtil {

    @Value("${com.icecream.auth.secretKey}")
    private String secretKey;
    private final RedisTemplate<String, String> redisTemplate;
    private final UserRepository userRepository;

    public JwtTokenDto generateTokenByFilterChain(Authentication authentication, int userId) {
        byte[] signingKey = secretKey.getBytes();

        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = (new Date()).getTime();

        Date accessTokenExpiresIn = new Date(now + 28800000);
        String accessToken = Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("authorities", authorities)
                .setExpiration(accessTokenExpiresIn)
                .signWith(Keys.hmacShaKeyFor(signingKey), SignatureAlgorithm.HS256)
                .compact();


        Date refreshTokenExpiresIn = new Date(now + 43200000);
        String refreshToken = Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setExpiration(refreshTokenExpiresIn)
                .claim("authorities", authorities)
                .signWith(Keys.hmacShaKeyFor(signingKey), SignatureAlgorithm.HS256)
                .compact();

        saveRefreshToken(authentication.getName(), refreshToken);

        return JwtTokenDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public JwtTokenDto generateTokenByController(String userId, String authorities) {
        byte[] signingKey = secretKey.getBytes();

        long now = (new Date()).getTime();

        Date accessTokenExpiresIn = new Date(now + 28800000);
        String accessToken = Jwts.builder()
                .setSubject(userId)
                .claim("authorities", authorities)
                .setExpiration(accessTokenExpiresIn)
                .signWith(Keys.hmacShaKeyFor(signingKey), SignatureAlgorithm.HS256)
                .compact();


        Date refreshTokenExpiresIn = new Date(now + 43200000);
        String refreshToken = Jwts.builder()
                .setSubject(userId)
                .setExpiration(refreshTokenExpiresIn)
                .claim("authorities", authorities)
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

        if (claims.get("authorities") == null) {
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }

        Collection<? extends GrantedAuthority> authorities = Arrays.stream(claims.get("authorities").toString().split(","))
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
        } catch (SecurityException e) {
            log.info("Invalid JWT Token", e);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token", e);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty.", e);
        }
        return false;
    }
//
//    public JwtToken reissueToken(String refreshToken) {
//
//        validateToken(refreshToken);
//
//        String userLoginId = parseClaims(refreshToken).getSubject();
//
//        com.e102.simcheonge_server.domain.user.entity.User user = userRepository.findByUserLoginId(userLoginId)
//                .orElseThrow(() -> new DataNotFoundException("해당 사용자가 존재하지 않습니다."));
//
//        // 직렬화
//
//        String redisRefreshToken = jwtUtil.findRefreshTokenInRedis(userLoginId);
//
//
//        if (ObjectUtils.isEmpty(redisRefreshToken)) {
//            throw new IllegalArgumentException(("reissue: 로그아웃 상태입니다: redis에 refresh token이 존재하지 않습니다"));
//        }
//        if (!redisRefreshToken.equals(refreshToken)) {
//            throw new IllegalArgumentException("reissue: refresh token이 redis에 저장된 refresh token과 다릅니다");
//        }
//
//        // 새로운 Authentication 객체 생성
//        UserDetails userDetails = new User(user.getUserLoginId(), "", Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"))); // 권한은 예시로 설정
//        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//
//        // 새로운 토큰 생성
//        JwtToken newToken = generateToken(authentication);
//
//        //새로운 토큰 반환
//        return newToken;
//    }
//

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

    public void saveRefreshToken(String deviceId, String refreshToken) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        valueOperations.set("refreshToken:" + deviceId, refreshToken, 12, TimeUnit.HOURS); // 1일 동안 유효
    }
//
//    public String findRefreshTokenInRedis(String userLoginId) {
//        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
//        return valueOperations.get("refreshToken:" + userLoginId);
//    }
//
//    public void invalidateRefreshToken(String userLoginId) {
//        redisTemplate.delete("refreshToken:" + userLoginId);
//    }
}