package org.example.rag_system_backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.UUID;


@Component
public class JwtProvider {

    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256); // In prod → load from config
    private final long jwtExpirationMs = 1000 * 60 * 15; // 15 minutes

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

