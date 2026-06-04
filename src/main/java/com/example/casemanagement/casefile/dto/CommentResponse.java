package com.example.casemanagement.casefile.dto;

import java.time.Instant;

public record CommentResponse(
        Long id,
        String author,
        String text,
        Instant createdAt
) {
}
