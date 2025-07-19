package com.esprit.articleservice.service;

import com.esprit.articleservice.client.StockFeignClient;
import com.esprit.articleservice.entity.Article;
import com.esprit.articleservice.exception.StockServiceException;
import com.esprit.articleservice.mapper.ArticleMapper;
import com.esprit.articleservice.repository.ArticleRepository;
import com.esprit.shared.dto.ArticleDto;
import com.esprit.shared.dto.StockDto;
import com.esprit.shared.events.StockUpdatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class IArticleServiceImp implements IArticleService {

    private final ArticleRepository repo;
    private final ArticleMapper mapper;
    @Qualifier("stockClient")
    private final StockFeignClient stockFeignClient;

    @Override
    public ArticleDto add(ArticleDto dto) {
        try {
            // 1. Check stock availability
            StockDto stock = stockFeignClient.getStockByArticleName(dto.title());
            if (stock == null || stock.quantity() <= 0) {
                throw new IllegalStateException("Insufficient stock for: " + dto.title());
            }

            // 2. Update stock quantity
            stockFeignClient.updateStockQuantity(
                    stock.id(),
                    Map.of("quantity", stock.quantity() - 1)
            );

            // 3. Create article
            Article article = mapper.mapToEntity(dto);
            article.setCreatedAt(LocalDateTime.now());
            return mapper.mapToDto(repo.save(article));

        } catch (Exception e) {
            log.error("Failed to create article: {}", e.getMessage());
            throw new IllegalStateException("Failed to process article: " + e.getMessage());
        }
    }


    @Override
    public ArticleDto update(Long id, Map<Object, Object> fields) {
        try {
            Article article = repo.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Article not found"));

            fields.forEach((key, value) -> {
                Field field = ReflectionUtils.findField(Article.class, (String) key);
                if (field != null) {
                    field.setAccessible(true);
                    ReflectionUtils.setField(field, article, value);
                }
            });

            article.setUpdatedAt(LocalDateTime.now());
            return mapper.mapToDto(repo.save(article));
        } catch (Exception e) {
            log.error("Error updating article {}: {}", id, e.getMessage());
            throw new IllegalStateException("Failed to update article", e);
        }
    }

    @Override
    public boolean delete(Long id) {
        try {
            if (!repo.existsById(id)) {
                return false;
            }
            repo.deleteById(id);
            return true;
        } catch (Exception e) {
            log.error("Error deleting article {}: {}", id, e.getMessage());
            throw new IllegalStateException("Failed to delete article", e);
        }
    }

    @Override
    public Page<ArticleDto> getArticles(int page, int size) {
        try {
            return repo.findAll(PageRequest.of(page, size))
                    .map(mapper::mapToDto);
        } catch (Exception e) {
            log.error("Error fetching articles: {}", e.getMessage());
            throw new IllegalStateException("Failed to retrieve articles", e);
        }
    }

    @Override
    public ArticleDto getArticle(Long id) {
        try {
            return repo.findById(id)
                    .map(mapper::mapToDto)
                    .orElseThrow(() -> new IllegalArgumentException("Article not found"));
        } catch (Exception e) {
            log.error("Error fetching article {}: {}", id, e.getMessage());
            throw new IllegalStateException("Failed to retrieve article", e);
        }
    }

    @Override
    public ArticleDto getArticleByTitle(String title) {
        try {
            return repo.findByTitle(title)
                    .map(mapper::mapToDto)
                    .orElseThrow(() -> new IllegalArgumentException("Article not found"));
        } catch (Exception e) {
            log.error("Error fetching article by title {}: {}", title, e.getMessage());
            throw new IllegalStateException("Failed to retrieve article", e);
        }
    }
}