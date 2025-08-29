package org.example.rag_system_backend.dtos;

import java.time.Instant;

public record QueeyLogDto(
        String queryText,
        String responseText,
        String sourcesJson,
        Instant createdAt
) {}
