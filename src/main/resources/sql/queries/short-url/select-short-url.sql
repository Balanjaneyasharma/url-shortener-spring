SELECT shortUrl
FROM ShortUrlTable
WHERE  hashedLongUrl = :hashedLongUrl;