package com.esprit.articleservice.service;

import com.esprit.shared.dto.ArticleDto;
import org.springframework.data.domain.Page;

import java.util.Map;

public interface IArticleService {
    ArticleDto add(ArticleDto dto);
    ArticleDto update(Long id, Map<Object, Object> fields);
    boolean delete(Long id);
    Page<ArticleDto> getArticles(int page, int size);
    ArticleDto getArticle(Long id);
    ArticleDto getArticleByTitle(String title);
}
