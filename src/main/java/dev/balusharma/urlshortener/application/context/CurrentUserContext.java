package dev.balusharma.urlshortener.application.context;

public record CurrentUserContext(
        Integer userId,
        String email,
        String name
) {}
