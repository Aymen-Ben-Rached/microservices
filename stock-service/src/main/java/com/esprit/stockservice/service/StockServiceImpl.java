package com.esprit.stockservice.service;

import com.esprit.shared.dto.StockDto;
import com.esprit.shared.events.StockUpdatedEvent;
import com.esprit.stockservice.entity.Stock;
import com.esprit.stockservice.mapper.StockMapper;
import com.esprit.stockservice.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockServiceImpl implements IStockService {

    private final StockRepository repository;
    private final StockMapper mapper;
    private final KafkaTemplate<String, StockUpdatedEvent> kafkaTemplate;

    @Override
    public StockDto add(StockDto dto) {
        Stock stock = mapper.toEntity(dto);
        stock.setCreatedAt(LocalDateTime.now());
        return mapper.toDto(repository.save(stock));
    }

    @Override
    public StockDto update(String id, Map<String, Integer> fields) {
        Stock stock = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Stock not found"));

        int oldQuantity = stock.getQuantity();

        fields.forEach((field, value) -> {
            if ("quantity".equals(field)) {
                stock.setQuantity(value);
            }
        });

        stock.setUpdatedAt(LocalDateTime.now());
        Stock updatedStock = repository.save(stock);

        // Publish event to Kafka
        publishStockUpdateEvent(updatedStock, oldQuantity);

        return mapper.toDto(updatedStock);
    }

    private void publishStockUpdateEvent(Stock updatedStock, int oldQuantity) {
        String updateType = updatedStock.getQuantity() > oldQuantity ? "INCREMENT" : "DECREMENT";

        StockUpdatedEvent event = new StockUpdatedEvent(
                updatedStock.getArticleName(),
                oldQuantity,
                updatedStock.getQuantity(),
                updateType
        );

        kafkaTemplate.send("stock-updated", event);
        log.info("Published StockUpdatedEvent: {}", event);
    }


    @Override
    public boolean delete(String id) {
        repository.deleteById(id);
        return !repository.existsById(id);
    }

    @Override
    public Page<StockDto> getStocks(int page, int size) {
        return repository.findAll(PageRequest.of(page, size))
                .map(mapper::toDto);
    }

    @Override
    public StockDto getStock(String id) {
        return repository.findById(id).map(mapper::toDto)
                .orElseThrow(() -> new IllegalArgumentException("Not found"));
    }

    @Override
    public StockDto getStockByArticleName(String name) {
        return repository.findByArticleName(name).map(mapper::toDto)
                .orElseThrow(() -> new IllegalArgumentException("Not found"));
    }
}
