package dev.balusharma.urlshortener.presentation.dto.response;

import java.time.LocalDateTime;

public record ApiErrorResponse(
        int status,
        String message,
        String path,
        LocalDateTime timestamp
) {}