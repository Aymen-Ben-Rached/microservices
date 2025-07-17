package com.esprit.articleservice.client;

import com.esprit.shared.dto.StockDto;
import com.esprit.articleservice.client.StockClient;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class StockClient {

    private final WebClient.Builder webClientBuilder;

    @CircuitBreaker(name = "stock", fallbackMethod = "fallbackStock")
    public StockDto getStockByArticleName(String articleName) {
        return webClientBuilder.build()
                .get()
                .uri("http://localhost:8082/stocks/article/{name}", articleName)
                .retrieve()
                .bodyToMono(StockDto.class)
                .block();
    }

    public StockDto fallbackStock(String articleName, Throwable throwable) {
        log.warn("Fallback triggered for stock lookup: {}", throwable.getMessage());
        return null;
    }

    @CircuitBreaker(name = "stock", fallbackMethod = "fallbackDecrement")
    public void decrementStockQuantity(String stockId, int newQuantity) {
        Map<String, Object> updateFields = Map.of("quantity", newQuantity);
        try {
            webClientBuilder.build()
                    .patch()
                    .uri("http://localhost:8082/stocks/{id}", stockId)
                    .bodyValue(updateFields)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to decrement stock quantity: " + e.getMessage());
        }
    }

    public void fallbackDecrement(String stockId, int newQuantity, Throwable throwable) {
        log.warn("⚠️ Fallback for decrementStockQuantity for stock ID {}: {}",
                stockId, throwable.getMessage());
    }

}
