package com.esprit.articleservice.client;

import com.esprit.articleservice.exception.StockServiceException;
import com.esprit.shared.dto.StockDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class StockFallback implements StockFeignClient {

    @Override
    public StockDto getStockByArticleName(String name) {
        log.error("CIRCUIT BREAKER ACTIVATED - Fallback for {}", name);
        throw new StockServiceException(
                "Stock service unavailable (Circuit Breaker)",
                "CB_ACTIVE"
        );
    }

    @Override
    public StockDto updateStockQuantity(String id, Map<String, Integer> updateFields) {
        log.error("CIRCUIT BREAKER ACTIVATED - Update blocked for {}", id);
        throw new StockServiceException(
                "Stock service unavailable (Circuit Breaker)",
                "CB_ACTIVE"
        );
    }
}