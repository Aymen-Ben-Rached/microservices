package com.esprit.articleservice.mapper;

import com.esprit.articleservice.entity.Article;
import com.esprit.shared.dto.ArticleDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ArticleMapper {
    ArticleMapper INSTANCE = Mappers.getMapper(ArticleMapper.class);

    Article mapToEntity(ArticleDto dto);
    ArticleDto mapToDto(Article entity);
}