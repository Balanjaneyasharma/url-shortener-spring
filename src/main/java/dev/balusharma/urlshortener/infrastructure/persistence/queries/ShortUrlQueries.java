package dev.balusharma.urlshortener.infrastructure.persistence.queries;

public final class ShortUrlQueries {

    private ShortUrlQueries() {}

    public static final String insertShortUrl = """
            INSERT INTO ShortUrlTable (
                    longUrl,
                    shortUrl,
                    hashedLongUrl,
                    userId
            )
            VALUES (:longUrl, :shortUrl, :hashedLongUrl, :userId);
    """;

    public static final String selectLongUrlByShortUrl = """
            SELECT longUrl
            From ShortUrlTable
            WHERE shortUrl = :shortUrl;
    """;

    public static final String selectShortUrlByHashedLongUrl = """
            SELECT shortUrl
            FROM ShortUrlTable
            WHERE  hashedLongUrl = :hashedLongUrl
                   AND userId = :userId;
    """;

    public static final String updateLongUrl = """
            UPDATE ShortUrlTable
            SET longUrl = :longUrl,
                hashedLongUrl = :hashedLongUrl
            WHERE shortUrl =:shortUrl
            AND userId = :userId;
    """;
}
