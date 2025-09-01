package org.example.rag_system_backend.controllers;

import org.example.rag_system_backend.dtos.DocumentDto;
import org.example.rag_system_backend.models.User;

import org.example.rag_system_backend.repositories.UserRepository;
import org.example.rag_system_backend.services.DocumentService;
import org.example.rag_system_backend.security.JwtProvider;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/files")
public class DocumentController {

    private final DocumentService documentService;
    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;

    public DocumentController(DocumentService documentService, JwtProvider jwtProvider, UserRepository userRepository) {
        this.documentService = documentService;
        this.jwtProvider = jwtProvider;
        this.userRepository = userRepository;
    }

    // GET /api/v1/files?page=0&size=20&filename=...&tag=...&status=...
    @GetMapping
    public ResponseEntity<Page<DocumentDto>> listDocuments(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String filename,
            @RequestParam(required = false) String tag,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to,
            @RequestParam(required = false) String sortBy
    ) {
        String token = authHeader.substring(7);
        String email = jwtProvider.getEmailFromToken(token);
        User user = userRepository.findByEmail(email).orElseThrow();

        Page<DocumentDto> docs = documentService.listForUser(user.getId(), filename, tag, status, from, to, page, size, sortBy);
        return ResponseEntity.ok(docs);
    }

    // GET /api/v1/files/{uuid}
    @GetMapping("/{uuid}")
    public ResponseEntity<DocumentDto> getDocument(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable UUID uuid
    ) {
        String token = authHeader.substring(7);
        String email = jwtProvider.getEmailFromToken(token);
        User user = userRepository.findByEmail(email).orElseThrow();

        return documentService.getByUuidForUser(uuid, user.getId())
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
