package org.example.rag_system_backend.controllers;

import lombok.RequiredArgsConstructor;
import org.example.rag_system_backend.dtos.UserDto;
import org.example.rag_system_backend.models.User;
import org.example.rag_system_backend.security.JwtProvider;
import org.example.rag_system_backend.services.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private final JwtProvider jwtProvider;
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@RequestBody User req) {
        req.setPasswordHash(encoder.encode(req.getPasswordHash()));
        req.setRoles("ROLE_USER");
        UserDto userDto=authService.saveUser(req);

        return ResponseEntity.ok(userDto);

    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String email, @RequestParam String password) {
        try {
            // Validate input parameters
            if (email == null || email.trim().isEmpty()) {
                return createErrorResponse("Email is required", HttpStatus.BAD_REQUEST);
            }
            if (password == null || password.trim().isEmpty()) {
                return createErrorResponse("Password is required", HttpStatus.BAD_REQUEST);
            }

            // Attempt login
            Optional<UserDto> userDto = authService.login(email, password);

            if (userDto.isPresent()) {
                // Successful login
                UserDto user = userDto.get();
                Map<String, Object> response = new HashMap<>();
                response.put("user", user);
                response.put("message", "Login successful");
                response.put("status", "success");

                return ResponseEntity.ok(response);
            } else {
                // Invalid credentials
                return createErrorResponse("Invalid email or password", HttpStatus.UNAUTHORIZED);
            }

        } catch (IllegalArgumentException e) {
            // Validation errors
            return createErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            // Internal server error
            System.err.println("Login error: " + e.getMessage());
            return createErrorResponse("An internal error occurred. Please try again later.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private ResponseEntity<Map<String, Object>> createErrorResponse(String message, HttpStatus status) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", message);
        errorResponse.put("status", "error");
        errorResponse.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.status(status).body(errorResponse);
    }

}
