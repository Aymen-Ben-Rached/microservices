package com.esprit.stockservice.controller;

import com.esprit.shared.dto.StockDto;
import com.esprit.stockservice.service.IStockService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/stocks")
@RequiredArgsConstructor
public class StockRestController {

    private final IStockService service;

    @PostMapping
    public StockDto add(@RequestBody StockDto dto) {
        return service.add(dto);
    }

    @PatchMapping("{id}")
    public StockDto update(@PathVariable String id, @RequestBody Map<Object, Object> fields) {
        return service.update(id, fields);
    }

    @DeleteMapping("{id}")
    public boolean delete(@PathVariable String id) {
        return service.delete(id);
    }

    @GetMapping
    public Page<StockDto> getAll(@RequestParam int page, @RequestParam int size) {
        return service.getStocks(page, size);
    }

    @GetMapping("{id}")
    public StockDto getById(@PathVariable String id) {
        return service.getStock(id);
    }

    @GetMapping("/article/{name}")
    public StockDto getByArticleName(@PathVariable String name) {
        return service.getStockByArticleName(name);
    }
}
