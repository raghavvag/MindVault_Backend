package org.example.rag_system_backend.messaging.events;

import lombok.*;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileUploadedEvent {
    private UUID eventId;          // idempotency / event id
    private int schemaVersion;     // 1
    private String eventType;      // "file.uploaded"
    private Instant occurredAt;

    private UUID docId;            // Document.uuid (public id)
    private Long userId;           // users.id
    private String filename;
    private String storagePath;    // e.g., "3/raghavfinal.pdf"
    private String fileType;
    private Long size;

    private Map<String, Object> metadata; // parsed metadata
    private String requestId;      // optional trace id (X-Request-Id)
}
