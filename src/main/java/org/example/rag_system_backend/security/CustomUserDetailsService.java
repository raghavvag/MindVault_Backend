package org.example.rag_system_backend.security;

import org.example.rag_system_backend.models.User;
import org.example.rag_system_backend.repositories.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    public CustomUserDetailsService(UserRepository repo) { this.userRepository = repo; }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Remove ROLE_ prefix since .roles() automatically adds it
        String[] roles = user.getRoles().split(",");
        for (int i = 0; i < roles.length; i++) {
            if (roles[i].startsWith("ROLE_")) {
                roles[i] = roles[i].substring(5); // Remove "ROLE_" prefix
            }
        }

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPasswordHash())
                .roles(roles)
                .disabled(!user.getEnabled())
                .build();
    }
}
