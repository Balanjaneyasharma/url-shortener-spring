UPDATE ShortUrlTable
SET longUrl = :updatedLongUrl
WHERE shortUrl =:shortUrl
AND userId = :userId;