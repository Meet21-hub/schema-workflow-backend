package com.schemaworkflow.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.schemaworkflow.entity.enums.Role;
import com.schemaworkflow.entity.enums.SubmissionStatus;

@Service
public class WorkflowService {

    // Defines allowed transitions: from status → list of (to status + allowed roles)
    private static final Map<SubmissionStatus, List<TransitionRule>> ALLOWED_TRANSITIONS = new HashMap<>();

    static {
        ALLOWED_TRANSITIONS.put(SubmissionStatus.DRAFT, List.of(
            new TransitionRule(SubmissionStatus.SUBMITTED, List.of(Role.SUBMITTER, Role.ADMIN))
        ));
        ALLOWED_TRANSITIONS.put(SubmissionStatus.SUBMITTED, List.of(
            new TransitionRule(SubmissionStatus.UNDER_REVIEW, List.of(Role.REVIEWER, Role.ADMIN))
        ));
        ALLOWED_TRANSITIONS.put(SubmissionStatus.UNDER_REVIEW, List.of(
            new TransitionRule(SubmissionStatus.APPROVED, List.of(Role.REVIEWER, Role.ADMIN)),
            new TransitionRule(SubmissionStatus.REJECTED, List.of(Role.REVIEWER, Role.ADMIN))
        ));
        ALLOWED_TRANSITIONS.put(SubmissionStatus.APPROVED, List.of()); // Terminal state
        ALLOWED_TRANSITIONS.put(SubmissionStatus.REJECTED, List.of(
            new TransitionRule(SubmissionStatus.DRAFT, List.of(Role.ADMIN)) // Admin can reopen
        ));
    }

    /**
     * Returns true if the given role is allowed to move a submission
     * from 'from' status to 'to' status.
     */
    public boolean canTransition(SubmissionStatus from, SubmissionStatus to, Role role) {
        List<TransitionRule> rules = ALLOWED_TRANSITIONS.getOrDefault(from, List.of());
        return rules.stream()
            .anyMatch(rule -> rule.toStatus() == to && rule.allowedRoles().contains(role));
    }

    /**
     * Validates a transition and throws an exception if not allowed.
     * Use this in your controller: call validate() instead of canTransition().
     */
    public void validateTransition(SubmissionStatus from, SubmissionStatus to, Role role) {
        if (!canTransition(from, to, role)) {
            throw new IllegalStateException(
                String.format(
                    "Transition from %s to %s is not allowed for role %s",
                    from, to, role
                )
            );
        }
    }

    // Inner record to hold transition rules
    private record TransitionRule(SubmissionStatus toStatus, List<Role> allowedRoles) {}
}