package dev.balusharma.urlshortener.presentation.dto.request;

import dev.balusharma.urlshortener.presentation.validation.ValidLongUrl;
import dev.balusharma.urlshortener.presentation.validation.ValidShortUrl;

public record UpdateUrlRequest(
        @ValidShortUrl
        String longUrl,

        @ValidLongUrl
        String shortUrl
) {
}
