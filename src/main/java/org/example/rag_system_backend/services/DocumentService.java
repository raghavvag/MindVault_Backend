package org.example.rag_system_backend.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.rag_system_backend.dtos.DocumentDto;
import org.example.rag_system_backend.messaging.IngestionEventPublisher;
import org.example.rag_system_backend.models.Document;
import org.example.rag_system_backend.models.User;
import org.example.rag_system_backend.repositories.DocumentRepository;
import org.example.rag_system_backend.repositories.spec.DocumentSpecification;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final ObjectMapper objectMapper;
    private final IngestionEventPublisher eventPublisher;

    public DocumentService(DocumentRepository documentRepository,
                           ObjectMapper objectMapper,
                           IngestionEventPublisher eventPublisher) {
        this.documentRepository = documentRepository;
        this.objectMapper = objectMapper;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Backwards-compatible create method (no requestId).
     * Delegates to the full createOnUpload(...) that accepts a requestId.
     */
    public DocumentDto createOnUpload(User user,
                                      String filename,
                                      String storagePath,
                                      String fileType,
                                      Long size,
                                      String metadataJson) {
        return createOnUpload(user, filename, storagePath, fileType, size, metadataJson, null);
    }

    /**
     * Create a Document row after upload confirmation and publish a file.uploaded event.
     * This method saves the Document with status = QUEUED and then attempts to publish the
     * ingestion event. Publish failures are logged and do not fail the HTTP flow.
     *
     * @param user         owning user entity (must be managed or reference)
     * @param filename     original filename
     * @param storagePath  storage path in MinIO/S3
     * @param fileType     content type
     * @param size         file size in bytes
     * @param metadataJson metadata JSON (string)
     * @param requestId    optional trace id (X-Request-Id) to include in event
     * @return DocumentDto representation of saved document
     */
    @Transactional
    public DocumentDto createOnUpload(User user,
                                      String filename,
                                      String storagePath,
                                      String fileType,
                                      Long size,
                                      String metadataJson,
                                      String requestId) {

        Document doc = new Document();
        doc.setUser(user);
        doc.setFilename(filename);
        doc.setStoragePath(storagePath);
        doc.setFileType(fileType);
        doc.setSize(size);
        doc.setStatus("QUEUED");
        doc.setMetadataJson(metadataJson);

        Document saved = documentRepository.save(doc);

        // Publish the file.uploaded event (best-effort: do not fail creation if publish fails)
        try {
            eventPublisher.publishFileUploaded(saved, requestId);
        } catch (Exception ex) {
            // Log the failure; keep document in QUEUED so it can be retried or recovered.
            // Replace with proper logging framework in your project (slf4j/logback).
            System.err.println("[WARN] Failed to publish file.uploaded event for doc="
                    + (saved.getUuid() != null ? saved.getUuid() : "unknown")
                    + " cause=" + ex.getMessage());
        }

        return new DocumentDto(saved);
    }

    public Optional<DocumentDto> getByUuidForUser(UUID uuid, Long userId) {
        return documentRepository.findByUuid(uuid)
                .filter(d -> d.getUser().getId().equals(userId))
                .map(DocumentDto::new);
    }

    public Page<DocumentDto> listForUser(Long userId,
                                         String filename,
                                         String tag,
                                         String status,
                                         Instant from,
                                         Instant to,
                                         int page, int size, String sortBy) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy == null ? "uploadedAt" : sortBy).descending());

        Specification<Document> spec = DocumentSpecification.belongsToUser(userId)
                .and(DocumentSpecification.filenameLike(filename))
                .and(DocumentSpecification.hasTag(tag))
                .and(DocumentSpecification.hasStatus(status))
                .and(DocumentSpecification.uploadedBetween(from, to));

        Page<Document> docs = documentRepository.findAll(spec, pageable);
        return docs.map(DocumentDto::new);
    }

    public DocumentDto updateStatus(UUID uuid, String newStatus) {
        Document d = documentRepository.findByUuid(uuid).orElseThrow();
        d.setStatus(newStatus);
        return new DocumentDto(documentRepository.save(d));
    }
}
