package com.example.casemanagement.common;

import java.time.Instant;
import java.util.Map;

public record ErrorResponse(
        String message,
        Instant timestamp,
        Map<String, String> details
) {
}
