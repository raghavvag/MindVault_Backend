package org.example.rag_system_backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;

@Component
public class ServiceJwtFilter extends OncePerRequestFilter {

    private final SecretKey serviceJwtSecret;

    public ServiceJwtFilter(SecretKey serviceJwtSecret) {
        this.serviceJwtSecret = serviceJwtSecret;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // Only protect /internal/*
        if (request.getRequestURI().startsWith("/internal/")) {
            String authHeader = request.getHeader("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                response.sendError(HttpStatus.UNAUTHORIZED.value(), "Missing service token");
                return;
            }

            String token = authHeader.substring(7);

            try {
                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(serviceJwtSecret)
                        .build()
                        .parseClaimsJws(token)
                        .getBody();

                if (!"ROLE_SERVICE".equals(claims.get("role"))) {
                    response.sendError(HttpStatus.FORBIDDEN.value(), "Invalid service role");
                    return;
                }

            } catch (Exception e) {
                response.sendError(HttpStatus.UNAUTHORIZED.value(), "Invalid service JWT");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
