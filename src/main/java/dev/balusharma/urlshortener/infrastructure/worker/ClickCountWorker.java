package dev.balusharma.urlshortener.infrastructure.worker;

import dev.balusharma.urlshortener.application.service.ClickCountProcessingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ClickCountWorker {

    @Autowired
    private ClickCountProcessingService processingService;

    @Scheduled(fixedDelayString = "${click-count.worker.fixed-delay}")
    public void processClickCounts() {
        processingService.processPendingBatch();
    }
}