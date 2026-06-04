package com.example.casemanagement.casefile.dto;

import jakarta.validation.constraints.NotBlank;

public record AddCommentRequest(
        @NotBlank String author,
        @NotBlank String text
) {
}
