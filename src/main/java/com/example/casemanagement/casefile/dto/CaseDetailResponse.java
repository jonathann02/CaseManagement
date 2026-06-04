package com.example.casemanagement.casefile.dto;

import com.example.casemanagement.casefile.CaseStatus;
import com.example.casemanagement.casefile.Priority;
import java.time.Instant;
import java.util.List;

public record CaseDetailResponse(
        Long id,
        String title,
        String description,
        String category,
        Priority priority,
        CaseStatus status,
        Instant createdAt,
        Instant updatedAt,
        String assignedTo,
        List<CommentResponse> comments,
        List<AuditLogResponse> auditLogs
) {
}
