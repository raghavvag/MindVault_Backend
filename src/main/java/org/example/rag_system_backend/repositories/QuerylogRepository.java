package org.example.rag_system_backend.repositories;

import org.example.rag_system_backend.models.Querylog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuerylogRepository extends JpaRepository<Querylog, Long> {
}
