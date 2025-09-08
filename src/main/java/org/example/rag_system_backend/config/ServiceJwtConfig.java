package org.example.rag_system_backend.config;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;

@Configuration
public class ServiceJwtConfig {

    @Bean
    public SecretKey serviceJwtSecret() {
        return Keys.secretKeyFor(SignatureAlgorithm.HS256);
    }
}
