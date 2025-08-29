package org.example.rag_system_backend.controllers;


import org.example.rag_system_backend.models.User;
import org.example.rag_system_backend.repositories.UserRepository;
import org.example.rag_system_backend.security.JwtProvider;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder=new BCryptPasswordEncoder();
    private final JwtProvider jwtProvider;
    public AuthController(UserRepository userRepository, JwtProvider jwtProvider) {
        this.userRepository = userRepository;
        this.jwtProvider = jwtProvider;
    }
    @PostMapping("/register")
    public String register(@RequestBody User req) {
        req.setPasswordHash(encoder.encode(req.getPasswordHash()));
        req.setRoles("ROLE_USER");
        userRepository.save(req);
        return "User registered successfully";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email, @RequestParam String password) {
        User user = userRepository.findByEmail(email).orElseThrow();
        if (encoder.matches(password, user.getPasswordHash())) {
            return jwtProvider.generateToken(user.getEmail(), user.getRoles());
        }
        throw new RuntimeException("Invalid credentials");
    }

}
