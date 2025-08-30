package org.example.rag_system_backend.security;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends GenericFilter {

    private final JwtProvider jwtProvider;
    private final CustomUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtProvider jwtProvider, CustomUserDetailsService uds) {
        this.jwtProvider = jwtProvider;
        this.userDetailsService = uds;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest http = (HttpServletRequest) request;
        String header = http.getHeader("Authorization");

        System.out.println("JWT Filter - Request URL: " + http.getRequestURL());
        System.out.println("JWT Filter - Authorization Header: " + header);

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            System.out.println("JWT Filter - Token extracted: " + token.substring(0, Math.min(20, token.length())) + "...");
            try {
                String email = jwtProvider.getEmailFromToken(token);
                System.out.println("JWT Filter - Email extracted: " + email);

                var userDetails = userDetailsService.loadUserByUsername(email);
                System.out.println("JWT Filter - User details loaded: " + userDetails.getUsername());

                var auth = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(http));
                SecurityContextHolder.getContext().setAuthentication(auth);

                System.out.println("JWT Filter - Authentication set successfully");
            } catch (Exception e) {
                System.err.println("JWT Filter - Authentication failed: " + e.getMessage());
                e.printStackTrace();
                ((HttpServletResponse) response).sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT");
                return;
            }
        } else {
            System.out.println("JWT Filter - No valid Authorization header found");
        }
        chain.doFilter(request, response);
    }
}

