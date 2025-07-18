package com.esprit.articleservice.client;

import com.esprit.articleservice.exception.StockServiceException;
import com.esprit.shared.dto.StockDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@FeignClient(
        name = "stock-service",
        contextId = "stockClient",
        fallback = StockFallback.class
)
public interface StockFeignClient {

    @GetMapping("/stocks/article/{name}")
    @CircuitBreaker(name = "stock", fallbackMethod = "getStockByArticleNameFallback")
    StockDto getStockByArticleName(@PathVariable String name);

    @PostMapping("/stocks/{id}/update-quantity")
    @CircuitBreaker(name = "stock", fallbackMethod = "updateStockQuantityFallback")
    StockDto updateStockQuantity(
            @PathVariable String id,
            @RequestBody Map<String, Integer> updateFields
    );

    // Fallback methods
    default StockDto getStockByArticleNameFallback(String name, Throwable throwable) {
        throw new StockServiceException("Service unavailable", "STOCK_SERVICE_DOWN");
    }

    default StockDto updateStockQuantityFallback(String id, Map<String, Integer> updateFields, Throwable throwable) {
        throw new StockServiceException("Update failed", "STOCK_UPDATE_FAILED");
    }
}