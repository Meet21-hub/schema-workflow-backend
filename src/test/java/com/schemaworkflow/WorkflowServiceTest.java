package com.schemaworkflow;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.schemaworkflow.entity.enums.Role;
import com.schemaworkflow.entity.enums.SubmissionStatus;
import com.schemaworkflow.service.WorkflowService;

class WorkflowServiceTest {

    private final WorkflowService mvnworkflowService = new WorkflowService();

    @Test
    void submitter_can_submit_draft() {
        assertTrue(workflowService.canTransition(
            SubmissionStatus.DRAFT, SubmissionStatus.SUBMITTED, Role.SUBMITTER
        ));
    }

    @Test
    void submitter_cannot_approve() {
        assertFalse(workflowService.canTransition(
            SubmissionStatus.UNDER_REVIEW, SubmissionStatus.APPROVED, Role.SUBMITTER
        ));
    }

    @Test
    void reviewer_can_approve_under_review() {
        assertTrue(workflowService.canTransition(
            SubmissionStatus.UNDER_REVIEW, SubmissionStatus.APPROVED, Role.REVIEWER
        ));
    }

    @Test
    void cannot_transition_from_approved() {
        assertFalse(workflowService.canTransition(
            SubmissionStatus.APPROVED, SubmissionStatus.DRAFT, Role.ADMIN
        ));
    }

    @Test
    void validate_throws_on_illegal_transition() {
        assertThrows(IllegalStateException.class, () ->
            workflowService.validateTransition(
                SubmissionStatus.SUBMITTED, SubmissionStatus.APPROVED, Role.SUBMITTER
            )
        );
    }
}