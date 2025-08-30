package org.example.rag_system_backend.controllers;

import io.minio.errors.MinioException;
import org.example.rag_system_backend.services.FileService;
import org.example.rag_system_backend.security.JwtProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/files")
public class FileController {

    private final FileService fileService;
    private final JwtProvider jwtProvider;

    public FileController(FileService fileService, JwtProvider jwtProvider) {
        this.fileService = fileService;
        this.jwtProvider = jwtProvider;
    }

    // Endpoint to generate presigned URL for file upload
    @GetMapping("/presign-upload")
    public ResponseEntity<?> getPresignedUploadUrl(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam String filename) {
        try {
            // Extract JWT token from Authorization header
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return createErrorResponse("Invalid Authorization header", HttpStatus.UNAUTHORIZED);
            }

            String token = authHeader.substring(7);
            String userEmail = jwtProvider.getEmailFromToken(token);

            System.out.println("Authenticated user email: " + userEmail);
            System.out.println("Filename: " + filename);

            String url = fileService.generatePresignedUrl(userEmail, filename);

            Map<String, Object> response = new HashMap<>();
            response.put("presignedUrl", url);
            response.put("userEmail", userEmail);
            response.put("filename", filename);
            response.put("status", "success");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("Error generating presigned URL: " + e.getMessage());
            e.printStackTrace();
            return createErrorResponse("Error generating presigned URL: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Endpoint to confirm file upload
    @PostMapping("/upload-complete")
    public ResponseEntity<?> confirmUpload(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam String filename) {
        try {
            // Extract JWT token from Authorization header
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return createErrorResponse("Invalid Authorization header", HttpStatus.UNAUTHORIZED);
            }

            String token = authHeader.substring(7);
            String userEmail = jwtProvider.getEmailFromToken(token);

            System.out.println("Confirming upload for user: " + userEmail);

            // Check if the file exists in MinIO
            boolean fileExists = fileService.confirmUpload(userEmail, filename);
            if (!fileExists) {
                return createErrorResponse("File not found in storage", HttpStatus.BAD_REQUEST);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("message", "File upload confirmed");
            response.put("userEmail", userEmail);
            response.put("filename", filename);
            response.put("status", "success");

            return ResponseEntity.ok(response);
        } catch (MinioException | IOException e) {
            System.err.println("Error confirming upload: " + e.getMessage());
            return createErrorResponse("Error confirming upload: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private ResponseEntity<Map<String, Object>> createErrorResponse(String message, HttpStatus status) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", message);
        errorResponse.put("status", "error");
        errorResponse.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.status(status).body(errorResponse);
    }
}
