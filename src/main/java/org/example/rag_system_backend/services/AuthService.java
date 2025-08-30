package org.example.rag_system_backend.services;

import lombok.RequiredArgsConstructor;
import org.example.rag_system_backend.dtos.UserDto;
import org.example.rag_system_backend.models.User;
import org.example.rag_system_backend.repositories.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserDto saveUser(User req) {
        userRepository.save(req);
        return new UserDto(req);
    }

    public Optional<UserDto> login(String email, String password) {
        try {
            // Validate input parameters
            if (email == null || email.trim().isEmpty()) {
                throw new IllegalArgumentException("Email cannot be null or empty");
            }
            if (password == null || password.trim().isEmpty()) {
                throw new IllegalArgumentException("Password cannot be null or empty");
            }

            // Find user by email
            Optional<User> userOptional = userRepository.findByEmail(email.trim());
            if (userOptional.isEmpty()) {
                return Optional.empty(); // User not found
            }

            User user = userOptional.get();

            // Verify password using BCrypt
            if (passwordEncoder.matches(password, user.getPasswordHash())) {
                return Optional.of(new UserDto(user));
            } else {
                return Optional.empty(); // Invalid password
            }

        } catch (IllegalArgumentException e) {
            throw e; // Re-throw validation errors
        } catch (Exception e) {
            // Log the error and return empty for any other exceptions
            System.err.println("Error during login: " + e.getMessage());
            return Optional.empty();
        }
    }
}
