package org.example.rag_system_backend.dtos;

import java.time.Instant;
import java.util.UUID;

public record DocumentDto(
        UUID uuid,
        String title,
        String filename,
        String fileType,
        Long size,
        String status,
        Instant uploadedAt
) {
}
