package dev.balusharma.urlshortener.domain.model;

import java.time.Instant;

public record User(
        Integer id,
        String email,
        String googleId,
        String name,
        Instant createdAt
) {
}