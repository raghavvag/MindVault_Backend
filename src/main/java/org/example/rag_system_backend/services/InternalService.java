package org.example.rag_system_backend.services;

import lombok.RequiredArgsConstructor;
import org.example.rag_system_backend.models.Document;
import org.example.rag_system_backend.repositories.DocumentRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InternalService {

    private final DocumentRepository documentRepository;

    public void markIngestionComplete(UUID docId, String status, String stats) {
        Document doc = documentRepository.findByUuid(docId)
                .orElseThrow(() -> new RuntimeException("Doc not found: " + docId));

        doc.setStatus(status);
        // store ingestion stats inside metadata_json for now
        String meta = (doc.getMetadataJson() == null ? "{}" : doc.getMetadataJson());
        doc.setMetadataJson(meta + " | ingestion=" + stats);

        documentRepository.save(doc);
    }
}
