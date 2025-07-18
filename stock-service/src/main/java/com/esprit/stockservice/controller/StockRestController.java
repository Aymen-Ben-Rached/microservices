package com.esprit.stockservice.controller;

import com.esprit.shared.dto.StockDto;
import com.esprit.stockservice.service.IStockService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/stocks")
@RequiredArgsConstructor
public class StockRestController {

    private final IStockService service;

    @PostMapping
    public ResponseEntity<StockDto> createStock(@RequestBody StockDto stockDto) {
        return ResponseEntity.ok(service.add(stockDto));
    }

    @PostMapping("/{id}/update-quantity")
    public ResponseEntity<StockDto> updateStockQuantity(
            @PathVariable String id,
            @RequestBody Map<String, Integer> updateFields) {

        if (!updateFields.containsKey("quantity")) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(service.update(id, updateFields));
    }

    @GetMapping("/{id}")
    public ResponseEntity<StockDto> getStockById(@PathVariable String id) {
        try {
            return ResponseEntity.ok(service.getStock(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/article/{name}")
    public ResponseEntity<StockDto> getStockByArticleName(@PathVariable String name) {
        try {
            return ResponseEntity.ok(service.getStockByArticleName(name));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStock(@PathVariable String id) {
        if (service.delete(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping
    public ResponseEntity<Page<StockDto>> getAllStocks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(service.getStocks(page, size));
    }
}