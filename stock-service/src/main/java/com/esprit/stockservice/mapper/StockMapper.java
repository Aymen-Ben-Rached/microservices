package com.esprit.stockservice.mapper;

import com.esprit.shared.dto.StockDto;
import com.esprit.stockservice.entity.Stock;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StockMapper {
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Stock toEntity(StockDto dto);

    StockDto toDto(Stock entity);
}