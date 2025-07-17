package com.esprit.stockservice.repository;

import com.esprit.stockservice.entity.Stock;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface StockRepository extends MongoRepository<Stock, String> {
    Optional<Stock> findByArticleName(String articleName);
}
