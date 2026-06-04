package com.example.casemanagement.casefile.dto;

import com.example.casemanagement.casefile.CaseStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateStatusRequest(@NotNull CaseStatus status) {
}
