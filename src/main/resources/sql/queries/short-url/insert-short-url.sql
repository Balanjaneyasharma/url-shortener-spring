INSERT INTO ShortUrlTable (
        longUrl,
        shortUrl,
        hashedLongUrl,
        userId
)
VALUES (:longUrl, :shortUrl, :hashedLongUrl, :userId);