package org.example.rag_system_backend.dtos;

import java.time.Instant;
import java.util.UUID;

public record UserDto(
        UUID uuid,
        String email,
        String fullName,
        String roles,
        Boolean enabled,
        Instant createdAt
){}
