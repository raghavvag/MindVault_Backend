package org.example.rag_system_backend.services;

import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.StatObjectArgs;
import io.minio.errors.*;
import io.minio.http.Method;
import jakarta.persistence.ManyToOne;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;

@Service
@RequiredArgsConstructor
public class FileService {
    private final MinioClient minioClient;

    public String generatePresignedUrl(String userId, String filename) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        String filepath = userId + "/" + filename;
        Duration expiry = Duration.ofMinutes(15);
        return minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .method(Method.PUT)
                        .bucket("documents")
                        .object(filepath)
                        .expiry((int) expiry.getSeconds())
                        .build()
        );
    }

    public boolean confirmUpload(String filename) throws MinioException, IOException {
        try {
            minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket("documents")
                            .object(filename)
                            .build()
            ); // Check if the file exists in the bucket
            return true; // File exists
        } catch (Exception e) {
            return false; // File doesn't exist or failed to access it
        }
    }
}