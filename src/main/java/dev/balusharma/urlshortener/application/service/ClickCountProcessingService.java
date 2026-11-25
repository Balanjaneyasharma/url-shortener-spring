package dev.balusharma.urlshortener.application.service;

import dev.balusharma.urlshortener.infrastructure.cache.repository.ClickCountCacheRepository;
import dev.balusharma.urlshortener.infrastructure.persistence.repository.ClickCountBatchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ClickCountProcessingService {

    @Autowired
    private ClickCountCacheRepository cacheRepository;
    @Autowired
    private ClickCountBatchRepository batchRepository;

    public void processPendingBatch() {

        String batchId = cacheRepository.claimBatch();

        if (batchId == null) {
            return;
        }

        Map<String, Long> clickCounts =
                cacheRepository.getBatch(batchId);

        if (clickCounts.isEmpty()) {
            return;
        }

        batchRepository.processBatch(batchId, clickCounts);

        cacheRepository.acknowledgeBatch(batchId);
    }
}