package dev.balusharma.urlshortener.infrastructure.model;

import java.time.Instant;

public class RefreshTokenModel {
    private Long id;
    private String hashedToken;
    private Long userId;
    private Instant expiresAt;
    private boolean revoked;
    private Instant createdAt;

    // Navigation property
    private UserModel user;
}
