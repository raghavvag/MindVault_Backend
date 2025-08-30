package org.example.rag_system_backend.dtos;

import org.example.rag_system_backend.models.Querylog;

import java.time.Instant;

public record QueryLogDto(
        String queryText,
        String responseText,
        String sourcesJson,
        Instant createdAt
) {
    public QueryLogDto(Querylog querylog){
        this(
                querylog.getQueryText(),
                querylog.getResponseText(),
                querylog.getSourcesJson(),
                querylog.getCreatedAt()
        );

    }
}
