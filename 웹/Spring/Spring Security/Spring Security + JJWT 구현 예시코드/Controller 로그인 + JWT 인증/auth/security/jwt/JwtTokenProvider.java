package com.e102.simcheonge_server.domain.auth.security.jwt;

import com.e102.simcheonge_server.common.exception.DataNotFoundException;
import com.e102.simcheonge_server.domain.auth.dto.JwtToken;
import com.e102.simcheonge_server.domain.user.repository.UserRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Slf4j
@Component
@AllArgsConstructor
public class JwtTokenProvider {
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private RedisTemplate<Object, Object> redisTemplate;

    // Member 정보를 가지고 AccessToken, RefreshToken을 생성하는 메서드
    public JwtToken generateToken(Authentication authentication) {

        byte[] signingKey = jwtUtil.getSecretKey().getBytes();

        // 권한 가져오기
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = (new Date()).getTime();

        // Access Token 생성
        Date accessTokenExpiresIn = new Date(now + 28800000);
        String accessToken = Jwts.builder()
                .setSubject(authentication.getName())
                .claim("auth", authorities) // 유저 권한에 따른 기능 분류 프로젝트에 반영 시 토큰에도 권한 반영하기
                .setExpiration(accessTokenExpiresIn)
                .signWith(Keys.hmacShaKeyFor(signingKey), SignatureAlgorithm.HS256)
                .compact();

        // Refresh Token 생성
        String refreshToken = Jwts.builder()
                .setSubject(authentication.getName())
                .setExpiration(new Date(now + 43200000))
                .claim("auth", authorities) // 유저 권한에 따른 기능 분류 프로젝트에 반영 시 토큰에도 권한 반영하기
                .signWith(Keys.hmacShaKeyFor(signingKey), SignatureAlgorithm.HS256)
                .compact();

        // Refresh Toekn Redis에 저장
        jwtUtil.saveRefreshToken(authentication.getName(), refreshToken);

        return JwtToken.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    // Jwt 토큰을 복호화하여 토큰에 들어있는 정보를 꺼내는 메서드
    public Authentication getAuthentication(String accessToken) {

        // Jwt 토큰 복호화
        Claims claims = parseClaims(accessToken);

        if (claims.get("auth") == null) {
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }

        // 클레임에서 권한 정보 가져오기
        Collection<? extends GrantedAuthority> authorities = Arrays.stream(claims.get("auth").toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        // UserDetails 객체를 만들어서 Authentication return
        // UserDetails: interface, User: UserDetails를 구현한 class
        UserDetails principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    // 토큰 정보를 검증하는 메서드
    public boolean validateToken(String token) {
        try {
            byte[] signingKey = jwtUtil.getSecretKey().getBytes();

            Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(signingKey))
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
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

    public JwtToken reissueToken(String refreshToken) {

        validateToken(refreshToken);

        String userLoginId = parseClaims(refreshToken).getSubject();

        com.e102.simcheonge_server.domain.user.entity.User user = userRepository.findByUserLoginId(userLoginId)
                .orElseThrow(() -> new DataNotFoundException("해당 사용자가 존재하지 않습니다."));

        // 직렬화

        String redisRefreshToken = jwtUtil.findRefreshTokenInRedis(userLoginId);


        if (ObjectUtils.isEmpty(redisRefreshToken)) {
            throw new IllegalArgumentException(("reissue: 로그아웃 상태입니다: redis에 refresh token이 존재하지 않습니다"));
        }
        if (!redisRefreshToken.equals(refreshToken)) {
            throw new IllegalArgumentException("reissue: refresh token이 redis에 저장된 refresh token과 다릅니다");
        }

        // 새로운 Authentication 객체 생성
        UserDetails userDetails = new User(user.getUserLoginId(), "", Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"))); // 권한은 예시로 설정
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        // 새로운 토큰 생성
        JwtToken newToken = generateToken(authentication);

        //새로운 토큰 반환
        return newToken;
        }

    // accessToken
    private Claims parseClaims(String accessToken) {
        try {
            byte[] signingKey = jwtUtil.getSecretKey().getBytes();
            return Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(signingKey))
                    .build()
                    .parseClaimsJws(accessToken)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

}