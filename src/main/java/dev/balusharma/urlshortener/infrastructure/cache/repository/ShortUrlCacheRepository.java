package dev.balusharma.urlshortener.infrastructure.cache.repository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

@Repository
public class ShortUrlCacheRepository {

    private static final String KEY_PREFIX = "short-url:cache:";

    private final StringRedisTemplate redisTemplate;
    private final Duration cacheTtl;

    public ShortUrlCacheRepository(
            StringRedisTemplate redisTemplate,
            @Value("${app.cache.short-url-ttl:1h}") Duration cacheTtl
    ) {
        this.redisTemplate = redisTemplate;
        this.cacheTtl = cacheTtl;
    }

    public Optional<String> getLongUrl(String shortUrl) {
        String longUrl = redisTemplate.opsForValue()
                .get(KEY_PREFIX + shortUrl);

        return Optional.ofNullable(longUrl);
    }

    public void save(String shortUrl, String longUrl) {
        redisTemplate.opsForValue()
                .set(KEY_PREFIX + shortUrl, longUrl, cacheTtl);
    }

    public void evict(String shortUrl) {
        redisTemplate.delete(KEY_PREFIX + shortUrl);
    }
}