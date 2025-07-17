package com.esprit.articleservice.mapper;

import com.esprit.articleservice.entity.Article;
import com.esprit.shared.dto.ArticleDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ArticleMapper {
    Article mapToEntity(ArticleDto dto);
    ArticleDto mapToDto(Article entity);
}
