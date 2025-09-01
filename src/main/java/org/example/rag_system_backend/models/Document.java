package org.example.rag_system_backend.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "documents")
@AllArgsConstructor
@NoArgsConstructor
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // public UUID used by clients
    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid = UUID.randomUUID();

    // link to user entity (ownership)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String title;

    @Column(nullable = false)
    private String filename;

    @Column(nullable = false)
    private String storagePath; // e.g., "bucket/user123/file.pdf"

    private String fileType;

    private Long size;

    @Column(nullable = false)
    private String status = "UPLOADED"; // UPLOADED, QUEUED, INDEXING, READY, FAILED

    private Instant uploadedAt = Instant.now();

    // flexible metadata stored as JSON text (tags, dates, pointers)
    @Lob
    @Column(name = "metadata_json")
    private String metadataJson;
}
