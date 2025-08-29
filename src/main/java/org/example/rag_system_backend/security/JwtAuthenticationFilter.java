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

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            try {
                String email = jwtProvider.getEmailFromToken(token);
                var userDetails = userDetailsService.loadUserByUsername(email);

                var auth = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(http));
                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (Exception e) {
                ((HttpServletResponse) response).sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT");
                return;
            }
        }
        chain.doFilter(request, response);
    }
}