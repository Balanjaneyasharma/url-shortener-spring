package dev.balusharma.urlshortener.infrastructure.cache.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class ClickCountCacheRepository {

    private static final String KEY = "short-url:click-counts";

    @Autowired
    private StringRedisTemplate redisTemplate;

    public Long increment(String shortUrl) {
        return redisTemplate.opsForHash()
                .increment(KEY, shortUrl, 1);
    }

    public Map<String, Long> getAll() {
        Map<Object, Object> entries =
                redisTemplate.opsForHash().entries(KEY);

        Map<String, Long> counts = new HashMap<>();

        entries.forEach((field, value) ->
                counts.put(
                        field.toString(),
                        Long.parseLong(value.toString())
                )
        );

        return counts;
    }
}
