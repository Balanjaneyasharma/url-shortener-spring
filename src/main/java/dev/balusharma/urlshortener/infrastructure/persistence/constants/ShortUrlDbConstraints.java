package dev.balusharma.urlshortener.infrastructure.persistence.constants;

public final class ShortUrlDbConstraints {

    private ShortUrlDbConstraints() {}

    public static final String DUPLICATE_SHORT_CODE =
            "ShortUrlTable_ShortUrl_key";
}