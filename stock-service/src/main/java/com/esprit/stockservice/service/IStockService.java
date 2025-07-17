package com.esprit.stockservice.service;

import com.esprit.shared.dto.StockDto;
import org.springframework.data.domain.Page;

import java.util.Map;

public interface IStockService {
    StockDto add(StockDto dto);
    StockDto update(String id, Map<Object, Object> fields);
    boolean delete(String id);
    Page<StockDto> getStocks(int page, int size);
    StockDto getStock(String id);
    StockDto getStockByArticleName(String name);
}
