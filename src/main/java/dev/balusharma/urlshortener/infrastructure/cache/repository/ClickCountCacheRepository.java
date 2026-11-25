package dev.balusharma.urlshortener.infrastructure.cache.repository;

import dev.balusharma.urlshortener.infrastructure.cache.scripts.ClickCountRedisScripts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
import java.util.HashMap;
import java.util.Map;

@Repository
public class ClickCountCacheRepository {

    private static final String ACTIVE_KEY = "short-url:click-counts";
    private static final String PROCESSING_KEY_PREFIX =
            "short-url:click-counts:processing:";
    private static final String PENDING_BATCH_KEY =
            "short-url:click-counts:pending-batch";

    @Autowired
    private StringRedisTemplate redisTemplate;

    public Long increment(String shortUrl) {
        return redisTemplate.opsForHash()
                .increment(ACTIVE_KEY, shortUrl, 1);
    }

    public Map<String, Long> getAll() {
        Map<Object, Object> entries =
                redisTemplate.opsForHash().entries(ACTIVE_KEY);

        Map<String, Long> counts = new HashMap<>();

        entries.forEach((field, value) ->
                counts.put(
                        field.toString(),
                        Long.parseLong(value.toString())
                )
        );

        return counts;
    }


    public String claimBatch() {
        String batchId = UUID.randomUUID().toString();

        String processingKey = PROCESSING_KEY_PREFIX + batchId;

        return redisTemplate.execute(
                ClickCountRedisScripts.CLAIM_BATCH,
                List.of(ACTIVE_KEY, PENDING_BATCH_KEY),
                batchId,
                processingKey
        );
    }

    public Map<String, Long> getBatch(String batchId) {
        Map<Object, Object> entries = redisTemplate.opsForHash()
                .entries(PROCESSING_KEY_PREFIX + batchId);

        Map<String, Long> counts = new HashMap<>();

        entries.forEach((field, value) ->
                counts.put(field.toString(), Long.parseLong(value.toString()))
        );

        return counts;
    }

    public boolean acknowledgeBatch(String batchId) {
        Long result = redisTemplate.execute(
                ClickCountRedisScripts.ACKNOWLEDGE_BATCH,
                List.of(PENDING_BATCH_KEY),
                batchId,
                PROCESSING_KEY_PREFIX + batchId
        );

        return result != null && result == 1;
    }
}
