package com.schemaworkflow.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.schemaworkflow.entity.AuditLog;

public interface AuditLogRepository extends JpaRepository<AuditLog, String> {

    List<AuditLog> findBySubmissionIdOrderByCreatedAtAsc(String submissionId);
}