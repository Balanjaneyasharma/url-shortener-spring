package dev.balusharma.urlshortener.infrastructure.persistence.queries;

public final class ShortUrlQueries {

    private ShortUrlQueries() {}

    public static final String insertShortUrl = """
            INSERT INTO "ShortUrlTable" (
                        "LongUrl",
                        "ShortUrl",
                        "HashedLongUrl",
                        "UserId"
            )
            VALUES (:longUrl, :shortUrl, :hashedLongUrl, :userId);
    """;

    public static final String selectLongUrlByShortUrl = """
            SELECT "LongUrl"
            From "ShortUrlTable"
            WHERE "ShortUrl" = :shortUrl;
    """;

    public static final String selectShortUrlByHashedLongUrl = """
            SELECT "ShortUrl"
            FROM "ShortUrlTable"
            WHERE "HashedLongUrl" = :hashedLongUrl
                   AND "UserId" = :userId;
    """;

    public static final String updateLongUrl = """
            UPDATE "ShortUrlTable"
            SET "LongUrl" = :longUrl,
                "HashedLongUrl = :hashedLongUrl
            WHERE "ShortUrl" =:shortUrl
            AND "UserId" = :userId;
    """;
}
