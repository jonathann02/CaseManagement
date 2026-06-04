package com.example.casemanagement.casefile.dto;

import com.example.casemanagement.casefile.CaseStatus;
import com.example.casemanagement.casefile.Priority;
import java.time.Instant;

public record CaseSummaryResponse(
        Long id,
        String title,
        String category,
        Priority priority,
        CaseStatus status,
        Instant createdAt,
        Instant updatedAt,
        String assignedTo
) {
}
