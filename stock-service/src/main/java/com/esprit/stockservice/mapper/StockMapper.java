package com.esprit.stockservice.mapper;

import com.esprit.shared.dto.StockDto;
import com.esprit.stockservice.entity.Stock;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StockMapper {
    Stock toEntity(StockDto dto);
    StockDto toDto(Stock stock);
}

