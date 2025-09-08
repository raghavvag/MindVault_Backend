package org.example.rag_system_backend.controllers;

import lombok.RequiredArgsConstructor;
import org.example.rag_system_backend.services.InternalService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/internal/docs")
@RequiredArgsConstructor
public class InternalController {

    private final InternalService internalService;

    @PostMapping("/{docId}/ingestion-complete")
    public ResponseEntity<?> ingestionComplete(
            @PathVariable UUID docId,
            @RequestBody Map<String, Object> payload) {

        String status = (String) payload.getOrDefault("status", "FAILED");
        String stats = payload.getOrDefault("stats", "").toString();

        internalService.markIngestionComplete(docId, status, stats);

        return ResponseEntity.ok(Map.of(
                "docId", docId,
                "status", status
        ));
    }
}
