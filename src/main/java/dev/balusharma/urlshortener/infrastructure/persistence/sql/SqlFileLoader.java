package dev.balusharma.urlshortener.infrastructure.persistence.sql;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SqlFileLoader {
    private final ResourceLoader resourceLoader;

    public SqlFileLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    private final Map<String, String> sqlCache = new ConcurrentHashMap<>();

    public String load(String filePath) {
        return sqlCache.computeIfAbsent(filePath, path -> {
            try {
                Resource resource = resourceLoader.getResource(
                        "classpath:" + path
                );

                try (InputStream inputStream = resource.getInputStream()) {
                    return new String(
                            inputStream.readAllBytes(),
                            StandardCharsets.UTF_8
                    );
                }
            } catch (IOException e) {
                throw new IllegalStateException(
                        "Failed to load SQL file: " + path, e
                );
            }
        });
    }
}
