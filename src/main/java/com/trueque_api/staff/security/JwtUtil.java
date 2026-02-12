package com.trueque_api.staff.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.trueque_api.staff.model.User;

import java.security.Key;
import java.util.Date;
import java.util.List;

@Component
public class JwtUtil {

    @Value("${jwt.secret-key}")
    private String secretKey;

    @Value("${jwt.expiration-time}")
    private long expirationTime;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public String generateToken(User user) {

        List<String> roles = user.getRoles()
                .stream()
                .map(role -> role.getName())
                .toList();

        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("userId", user.getId().toString())
                .claim("roles", roles)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractEmail(String token) {
            return extractAllClaims(token).getSubject();
        }

        public List<String> extractRoles(String token) {
        Claims claims = extractAllClaims(token);
        Object roles = claims.get("roles");

        if (roles instanceof List<?>) {
            return ((List<?>) roles)
                    .stream()
                    .map(Object::toString)
                    .toList();
        }

        return List.of();
    }

    public boolean validateToken(String token) {
        try {
            extractAllClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}