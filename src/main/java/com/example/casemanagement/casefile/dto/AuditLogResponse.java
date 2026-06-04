package com.example.casemanagement.casefile.dto;

import com.example.casemanagement.audit.AuditAction;
import java.time.Instant;

public record AuditLogResponse(
        Long id,
        AuditAction action,
        String message,
        Instant createdAt
) {
}
