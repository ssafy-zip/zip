package com.ssafy.BaeAndChoi.config.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {
    // 비밀키(길이 256비트 이상 권장)
    private final Key secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    // 토큰 유효시간 (예: 1시간)
    private final long expirationMs = 1000 * 60 * 60;

    // 토큰에서 username 추출
    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                   .setSigningKey(secretKey)
                   .build()
                   .parseClaimsJws(token)
                   .getBody()
                   .getSubject();
    }

    // 토큰 생성
    public String generateToken(String username, Map<String, Object> extraClaims) {
        return Jwts.builder()
                   .setClaims(extraClaims)
                   .setSubject(username)
                   .setIssuedAt(new Date())
                   .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                   .signWith(secretKey)
                   .compact();
    }

    // 토큰 유효성 검사
    public boolean validateToken(String token, String username) {
        try {
            final String extractedUsername = extractUsername(token);
            return (extractedUsername.equals(username) && 
                    !isTokenExpired(token));
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        Date expiration = Jwts.parserBuilder()
                              .setSigningKey(secretKey)
                              .build()
                              .parseClaimsJws(token)
                              .getBody()
                              .getExpiration();
        return expiration.before(new Date());
    }
}
