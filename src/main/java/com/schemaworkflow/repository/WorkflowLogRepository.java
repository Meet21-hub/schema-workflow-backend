package com.schemaworkflow.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.schemaworkflow.entity.WorkflowLog;

public interface WorkflowLogRepository extends JpaRepository<WorkflowLog, String> {

    List<WorkflowLog> findBySubmissionIdOrderByCreatedAtAsc(String submissionId);
}