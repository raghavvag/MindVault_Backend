package org.example.rag_system_backend.messaging;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.rag_system_backend.messaging.events.FileUploadedEvent;
import org.example.rag_system_backend.models.Document;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class IngestionEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${mindvault.messaging.exchange}")
    private String exchange;

    @Value("${mindvault.messaging.routing-key}")
    private String routingKey;

    private final ObjectMapper mapper = new ObjectMapper();

    public void publishFileUploaded(Document doc, String requestId) {
        Map<String, Object> metadata = null;
        try {
            if (doc.getMetadataJson() != null && !doc.getMetadataJson().isBlank()) {
                metadata = mapper.readValue(doc.getMetadataJson(), new TypeReference<Map<String,Object>>() {});
            }
        } catch (Exception e) {
            metadata = Map.of("raw", doc.getMetadataJson());
        }

        FileUploadedEvent event = FileUploadedEvent.builder()
                .eventId(UUID.randomUUID())
                .schemaVersion(1)
                .eventType("file.uploaded")
                .occurredAt(Instant.now())
                .docId(doc.getUuid())
                .userId(doc.getUser().getId())
                .filename(doc.getFilename())
                .storagePath(doc.getStoragePath())
                .fileType(doc.getFileType())
                .size(doc.getSize())
                .metadata(metadata)
                .requestId(requestId)
                .build();

        CorrelationData correlation = new CorrelationData(event.getEventId().toString());
        rabbitTemplate.convertAndSend(exchange, routingKey, event, correlation);
    }
}
