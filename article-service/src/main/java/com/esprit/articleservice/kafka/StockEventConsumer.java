package com.esprit.articleservice.kafka;

import com.esprit.shared.events.StockUpdatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StockEventConsumer {

    @KafkaListener(topics = "stock-updated-clean", groupId = "article-service-group")
    public void handleStockUpdated(StockUpdatedEvent event) {
        log.info("""
            Received Stock Update:
            Article: {}
            Change: {} ({} -> {})
            Type: {}
            """,
                event.articleName(),
                event.newQuantity() - event.oldQuantity(),
                event.oldQuantity(),
                event.newQuantity(),
                event.updateType());

        // Here you can:
        // 1. Update article cache
        // 2. Send notifications
        // 3. Trigger business logic
    }
}