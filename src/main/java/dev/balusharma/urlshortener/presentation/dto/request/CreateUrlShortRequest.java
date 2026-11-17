package dev.balusharma.urlshortener.presentation.dto.request;

import dev.balusharma.urlshortener.presentation.validation.ValidLongUrl;

public record CreateUrlShortRequest(
        @ValidLongUrl String longUrl
) { }
