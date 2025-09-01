package org.example.rag_system_backend.dtos;

import org.example.rag_system_backend.models.Document;

import java.time.Instant;
import java.util.UUID;

public record DocumentDto(
        UUID uuid,
        String title,
        String filename,
        String fileType,
        Long size,
        String status,
        String metadataJson,
        Instant uploadedAt
) {
    public DocumentDto(Document document) {
        this(
                document.getUuid(),
                document.getTitle(),
                document.getFilename(),
                document.getFileType(),
                document.getSize(),
                document.getStatus(),
                document.getMetadataJson(),
                document.getUploadedAt()
        );
    }
}