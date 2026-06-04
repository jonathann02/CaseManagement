package com.example.casemanagement.casefile.dto;

import com.example.casemanagement.casefile.Priority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateCaseRequest(
        @NotBlank String title,
        @NotBlank String description,
        @NotBlank String category,
        @NotNull Priority priority,
        @NotBlank String assignedTo
) {
}
