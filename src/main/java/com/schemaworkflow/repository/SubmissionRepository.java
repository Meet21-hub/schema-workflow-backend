package com.schemaworkflow.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.schemaworkflow.entity.Submission;
import com.schemaworkflow.entity.enums.SubmissionStatus;

public interface SubmissionRepository extends JpaRepository<Submission, String> {

    List<Submission> findByStatus(SubmissionStatus status);

    List<Submission> findBySubmitterId(String submitterId);

    long countByStatus(SubmissionStatus status);
}