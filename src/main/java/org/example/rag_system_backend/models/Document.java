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

    // Let DB handle UUID (via gen_random_uuid())
    @Column(unique = true, updatable = false)
    private UUID uuid;

    // link to user entity (ownership)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String title;

    @Column(nullable = false)
    private String filename;

    @Column(nullable = false, name = "storage_path")
    private String storagePath;

    @Column(name = "file_type", length = 128)
    private String fileType;

    private Long size;

    @Column(nullable = false, length = 32)
    private String status = "QUEUED"; // QUEUED, PROCESSING, READY, FAILED

    @Column(name = "uploaded_at")
    private Instant uploadedAt = Instant.now();

    // flexible metadata stored as JSON text (tags, dates, pointers)
    @Lob
    @Column(name = "metadata_json")
    private String metadataJson;
}
