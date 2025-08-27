package org.example.rag_system_backend.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.naming.Name;
import java.time.Instant;

@Table(name = "querylogs")
@Entity
@AllArgsConstructor
@NoArgsConstructor

public class Querylog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String queryText;

    @Column(columnDefinition = "TEXT")
    private String responseText;

    @Column(columnDefinition = "TEXT")
    private String sourcesJson;

    private Instant createdAt = Instant.now();
}
