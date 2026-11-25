package dev.balusharma.urlshortener.application.service;

import dev.balusharma.urlshortener.infrastructure.cache.repository.ClickCountCacheRepository;
import dev.balusharma.urlshortener.infrastructure.persistence.repository.ClickCountBatchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.util.Map;

@Service
public class ClickCountProcessingService {

    private static final Logger logger = LoggerFactory.getLogger(ClickCountProcessingService.class);

    @Autowired
    private ClickCountCacheRepository clicksCountCacheRepository;
    @Autowired
    private ClickCountBatchRepository clickCountBatchRepository;

    @Value("${click-count.worker.batch-retention-days}")
    private int batchRetentionDays;

    public void processPendingBatch() {

        // A single Redis pending key serializes all application instances. Other instances
        // can see the same batch and safely retry it because database processing is idempotent.
        String batchId = clicksCountCacheRepository.claimBatch();

        if (batchId == null) {
            return;
        }

        Map<String, Long> clickCounts =
                clicksCountCacheRepository.getBatch(batchId);

        if (clickCounts.isEmpty()) {
            logger.warn("Acknowledging empty click count batch {}", batchId);
            acknowledge(batchId);
            return;
        }

        clickCountBatchRepository.processBatch(batchId, clickCounts);

        acknowledge(batchId);
    }

    private void acknowledge(String batchId) {
        if (!clicksCountCacheRepository.acknowledgeBatch(batchId)) {
            logger.warn("Click count batch {} was processed but Redis did not acknowledge it", batchId);
        }
    }

    public int cleanupProcessedBatches() {
        return clickCountBatchRepository.deleteProcessedBatchesOlderThan(batchRetentionDays);
    }
}
