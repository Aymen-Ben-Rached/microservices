package com.esprit.stockservice.service;

import com.esprit.shared.dto.StockDto;
import com.esprit.stockservice.entity.Stock;
import com.esprit.stockservice.mapper.StockMapper;
import com.esprit.stockservice.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StockServiceImpl implements IStockService {

    private final StockRepository repository;
    private final StockMapper mapper;

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

        fields.forEach((field, value) -> {
            if ("quantity".equals(field)) {
                stock.setQuantity(value);
            }
        });

        stock.setUpdatedAt(LocalDateTime.now());
        return mapper.toDto(repository.save(stock));
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
