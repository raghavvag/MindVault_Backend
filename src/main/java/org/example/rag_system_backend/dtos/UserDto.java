package org.example.rag_system_backend.dtos;

import org.example.rag_system_backend.models.User;

import java.time.Instant;
import java.util.UUID;

public record UserDto(
        UUID uuid,
        String email,
        String fullName,
        String roles,
        Boolean enabled,
        Instant createdAt
){
    public UserDto(User user){
        this(
                user.getUuid(),
                user.getEmail(),
                user.getFullName(),
                user.getRoles(),
                user.getEnabled(),
                user.getCreatedAt()
        );
    }
}
