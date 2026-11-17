package dev.balusharma.urlshortener.infrastructure.model;

import java.time.Instant;

public class ShortUrlModel {
    private Long id;
    private String shortUrl;
    private String longUrl;
    private String hashedLongUrl;
    private Long userId;
    private Instant createdAt;
    private Instant updatedAt;

    // Navigation Property
    private UserModel user;
}