package org.example.rag_system_backend.repositories;

import org.example.rag_system_backend.models.Document;
import org.example.rag_system_backend.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface DocumentRepository extends JpaRepository<Document, Long>, JpaSpecificationExecutor<Document> {
    Optional<Document> findByUuid(UUID uuid);
    // simple helper to fetch by user with pagination handled by Spring Data
}
