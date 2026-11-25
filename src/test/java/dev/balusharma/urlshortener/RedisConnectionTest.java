package dev.balusharma.urlshortener;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(
        properties = "app.security.jwt.access-token-expiry-minutes=15",
        args = "--spring.profiles.active=development"
)
class RedisConnectionTest {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Test
    void shouldConnectToRedisAndPerformOperations() {

        String key = "test:redis:connection:" + UUID.randomUUID();

        try {
            redisTemplate.opsForValue().set(key, "connected");

            String result = redisTemplate.opsForValue().get(key);

            assertEquals("connected", result);
        }
        finally {
            redisTemplate.delete(key);
        }
    }
}

