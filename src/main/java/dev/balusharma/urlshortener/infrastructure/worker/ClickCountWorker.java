package dev.balusharma.urlshortener.infrastructure.worker;

import dev.balusharma.urlshortener.application.service.ClickCountProcessingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class ClickCountWorker {

    private static final Logger logger = LoggerFactory.getLogger(ClickCountWorker.class);

    @Autowired
    private ClickCountProcessingService processingService;

    @Scheduled(fixedDelayString = "${click-count.worker.fixed-delay}")
    public void processClickCounts() {
        try {
            processingService.processPendingBatch();
        } catch (RuntimeException ex) {
            logger.error("Failed to process pending click count batch", ex);
        }
    }

    @Scheduled(fixedDelayString = "${click-count.worker.cleanup-fixed-delay}")
    public void cleanupProcessedBatches() {
        try {
            int deleted = processingService.cleanupProcessedBatches();
            if (deleted > 0) {
                logger.info("Deleted {} click count batch idempotency records", deleted);
            }
        } catch (RuntimeException ex) {
            logger.error("Failed to clean up old click count batch records", ex);
        }
    }
}
