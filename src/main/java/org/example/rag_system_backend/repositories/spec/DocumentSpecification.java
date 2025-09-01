package org.example.rag_system_backend.repositories.spec;

import org.example.rag_system_backend.models.Document;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Expression;
import java.time.Instant;

public class DocumentSpecification {

    public static Specification<Document> belongsToUser(Long userId) {
        return (root, query, cb) -> cb.equal(root.get("user").get("id"), userId);
    }

    public static Specification<Document> filenameLike(String filename) {
        return (root, query, cb) -> {
            if (filename == null || filename.isBlank()) return cb.conjunction();
            return cb.like(cb.lower(root.get("filename")), "%" + filename.toLowerCase() + "%");
        };
    }

    public static Specification<Document> hasStatus(String status) {
        return (root, query, cb) -> {
            if (status == null || status.isBlank()) return cb.conjunction();
            return cb.equal(root.get("status"), status);
        };
    }

    // crude metadata tag check - checks if metadataJson contains the tag string (case-insensitive)
    public static Specification<Document> hasTag(String tag) {
        return (root, query, cb) -> {
            if (tag == null || tag.isBlank()) return cb.conjunction();
            Expression<String> expr = cb.lower(root.get("metadataJson").as(String.class));
            return cb.like(expr, "%\"" + tag.toLowerCase() + "\"%"); // looks for "tag" in JSON
        };
    }

    public static Specification<Document> uploadedBetween(Instant from, Instant to) {
        return (root, query, cb) -> {
            if (from == null && to == null) return cb.conjunction();
            if (from != null && to != null) return cb.between(root.get("uploadedAt"), from, to);
            if (from != null) return cb.greaterThanOrEqualTo(root.get("uploadedAt"), from);
            return cb.lessThanOrEqualTo(root.get("uploadedAt"), to);
        };
    }
}