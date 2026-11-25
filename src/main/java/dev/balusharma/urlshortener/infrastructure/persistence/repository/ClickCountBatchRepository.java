package dev.balusharma.urlshortener.infrastructure.persistence.repository;

import dev.balusharma.urlshortener.infrastructure.persistence.queries.ClicksCountBatchQueries;
import dev.balusharma.urlshortener.infrastructure.persistence.queries.ShortUrlQueries;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Repository
public class ClickCountBatchRepository {

    private static final Logger logger = LoggerFactory.getLogger(ClickCountBatchRepository.class);

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Transactional
    public boolean processBatch(
            String batchId,
            Map<String, Long> clickCounts
    ) {

        int inserted = jdbcTemplate.update(
                ClicksCountBatchQueries.insertBatch,
                new MapSqlParameterSource()
                        .addValue("batchId", UUID.fromString(batchId))
        );

        // This batch was already committed previously.
        if (inserted == 0) {
            return false;
        }

        for (Map.Entry<String, Long> entry : clickCounts.entrySet()) {

            int updated = jdbcTemplate.update(
                    ShortUrlQueries.incrementClicksCount,
                    new MapSqlParameterSource()
                            .addValue("shortUrl", entry.getKey())
                            .addValue("count", entry.getValue())
            );

            if (updated == 0) {
                logger.warn("Skipping click count for missing short URL {} in batch {}", entry.getKey(), batchId);
            } else if (updated != 1) {
                throw new IllegalStateException("Unexpected row count updating click count for " + entry.getKey());
            }
        }

        return true;
    }

    public int deleteProcessedBatchesOlderThan(int retentionDays) {
        return jdbcTemplate.update(
                ClicksCountBatchQueries.deleteOldClickCountBatches,
                new MapSqlParameterSource().addValue("retentionDays", retentionDays)
        );
    }
}
