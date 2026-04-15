package com.unqiuehire.kashflow.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

    // Keep this at least 32+ characters for HS256
    private static final String SECRET_KEY =
            "my-super-secret-key-for-jwt-token-123456";

    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 10; // 10 hours

    private final SecretKey key = Keys.hmacShaKeyFor(
            SECRET_KEY.getBytes(StandardCharsets.UTF_8)
    );

    public String generateToken(Long userId, String phoneNumber, String role) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + EXPIRATION_TIME);

        return Jwts.builder()
                .claims()
                .subject(phoneNumber)
                .add("userId", userId)
                .add("role", role)
                .expiration(expiryDate)
                .issuedAt(now)
                .and()
                .signWith(key)
                .compact();
    }

    public String extractPhoneNumber(String token) {
        return extractAllClaims(token).getSubject();
    }

    public String extractRole(String token) {
        Object role = extractAllClaims(token).get("role");
        return role != null ? role.toString() : null;
    }

    public Long extractUserId(String token) {
        Object userId = extractAllClaims(token).get("userId");
        if (userId instanceof Integer) {
            return ((Integer) userId).longValue();
        }
        if (userId instanceof Long) {
            return (Long) userId;
        }
        return null;
    }

    public boolean isTokenValid(String token, String phoneNumber) {
        String extractedPhoneNumber = extractPhoneNumber(token);
        return extractedPhoneNumber != null
                && extractedPhoneNumber.equals(phoneNumber)
                && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        Date expiration = extractAllClaims(token).getExpiration();
        return expiration != null && expiration.before(new Date());
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}