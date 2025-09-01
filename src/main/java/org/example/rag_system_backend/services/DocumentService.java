package org.example.rag_system_backend.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.rag_system_backend.dtos.DocumentDto;
import org.example.rag_system_backend.models.Document;
import org.example.rag_system_backend.models.User;
import org.example.rag_system_backend.repositories.DocumentRepository;
import org.example.rag_system_backend.repositories.spec.DocumentSpecification;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final ObjectMapper objectMapper;

    public DocumentService(DocumentRepository documentRepository, ObjectMapper objectMapper) {
        this.documentRepository = documentRepository;
        this.objectMapper = objectMapper;
    }

    public DocumentDto createOnUpload(User user, String filename, String storagePath, String fileType, Long size, String metadataJson) {
        Document doc = new Document();
        doc.setUser(user);
        doc.setFilename(filename);
        doc.setStoragePath(storagePath);
        doc.setFileType(fileType);
        doc.setSize(size);
        doc.setStatus("QUEUED");
        doc.setMetadataJson(metadataJson);
        Document saved = documentRepository.save(doc);
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