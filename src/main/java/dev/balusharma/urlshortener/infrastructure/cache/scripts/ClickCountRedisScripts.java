package dev.balusharma.urlshortener.infrastructure.cache.scripts;

import org.springframework.data.redis.core.script.DefaultRedisScript;

public final class ClickCountRedisScripts {

    private ClickCountRedisScripts() {}

    public static final DefaultRedisScript<String> CLAIM_BATCH =
            new DefaultRedisScript<>("""
                    local activeBatchKey = KEYS[1]
                    local pendingBatchKey = KEYS[2]

                    local batchId = ARGV[1]
                    local processingBatchKey = ARGV[2]

                    local pending = redis.call('GET', pendingBatchKey)

                    if pending then
                        return pending
                    end

                    if redis.call('EXISTS', activeBatchKey) == 0 then
                        return nil
                    end

                    if redis.call('HLEN', activeBatchKey) == 0 then
                        return nil
                    end

                    redis.call('RENAME', activeBatchKey, processingBatchKey)
                    redis.call('SET', pendingBatchKey, batchId)

                    return batchId
                    """, String.class);

    public static final DefaultRedisScript<Long> ACKNOWLEDGE_BATCH =
            new DefaultRedisScript<>("""
                    local pendingBatchKey = KEYS[1]

                    local batchId = ARGV[1]
                    local processingBatchKey = ARGV[2]

                    local pending = redis.call('GET', pendingBatchKey)

                    if pending == batchId then
                        redis.call('DEL', processingBatchKey)
                        redis.call('DEL', pendingBatchKey)
                        return 1
                    end

                    return 0
                    """, Long.class);
}