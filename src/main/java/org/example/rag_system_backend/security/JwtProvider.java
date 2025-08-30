package org.example.rag_system_backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtProvider {

    private final Key key;
    private final long jwtExpirationMs = 1000 * 60 * 15; // 15 minutes

    public JwtProvider(@Value("${jwt.secret:mySecretKey123456789012345678901234567890}") String secret) {
        // Ensure the secret is at least 32 bytes for HS256
        if (secret.length() < 32) {
            secret = secret + "0".repeat(32 - secret.length());
        }
        // Create key directly from secret bytes
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateToken(String email, String roles) {
        return Jwts.builder()
                .setId(UUID.randomUUID().toString())
                .setSubject(email)
                .claim("roles", roles)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(key)
                .compact();
    }

    public Jws<Claims> validateToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
    }

    public String getEmailFromToken(String token) {
        return validateToken(token).getBody().getSubject();
    }
}
