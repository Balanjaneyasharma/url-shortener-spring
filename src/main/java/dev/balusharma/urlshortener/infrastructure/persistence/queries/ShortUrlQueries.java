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

    public static final String selectLongUrl = """
            SELECT LongUrl
            From ShortUrlTable
            WHERE shortUrl = :shortUrl;
    """;

    public static final String selectShortURl = """
            SELECT shortUrl
            FROM ShortUrlTable
            WHERE  hashedLongUrl = :hashedLongUrl;
    """;

    public static final String updateLongUrl = """
            UPDATE ShortUrlTable
            SET longUrl = :updatedLongUrl
            WHERE shortUrl =:shortUrl
            AND userId = :userId;
    """;
}
