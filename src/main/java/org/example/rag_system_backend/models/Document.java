package org.example.rag_system_backend.models;

import jakarta.persistence.*;
import lombok.*;
import org.example.rag_system_backend.models.User;

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

    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid = UUID.randomUUID();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String title;

    @Column(nullable = false)
    private String filename;

    @Column(nullable = false)
    private String storagePath; // s3://bucket/path

    private String fileType;

    private Long size;

    @Column(nullable = false)
    private String status = "UPLOADED"; // UPLOADED, QUEUED, INDEXING, READY, FAILED

    private Instant uploadedAt = Instant.now();

    @Lob
    private String metadataJson;
}
