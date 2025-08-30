package org.example.rag_system_backend.services;

import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.StatObjectArgs;
import io.minio.errors.*;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;

@Service
@RequiredArgsConstructor
public class FileService {
    private final MinioClient minioClient;

    @Value("${minio.bucket}")
    private String bucketName;

    public String generatePresignedUrl(String userId, String filename) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        String filepath = userId + "/" + filename;
        Duration expiry = Duration.ofMinutes(15);
        return minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .method(Method.PUT)
                        .bucket(bucketName)
                        .object(filepath)
                        .expiry((int) expiry.getSeconds())
                        .build()
        );
    }

    public boolean confirmUpload(String userId, String filename) throws MinioException, IOException {
        try {
            String filepath = userId + "/" + filename;
            minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucketName)
                            .object(filepath)
                            .build()
            ); // Check if the file exists in the bucket
            return true; // File exists
        } catch (Exception e) {
            return false; // File doesn't exist or failed to access it
        }
    }
}

