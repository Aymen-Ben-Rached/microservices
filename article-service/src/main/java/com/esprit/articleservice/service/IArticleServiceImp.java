package com.esprit.articleservice.service;

import com.esprit.articleservice.entity.Article;
import com.esprit.articleservice.mapper.ArticleMapper;
import com.esprit.articleservice.repository.ArticleRepository;
import com.esprit.shared.dto.ArticleDto;
import com.esprit.shared.dto.StockDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.reactive.function.client.WebClient;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class IArticleServiceImp implements IArticleService {
    private final ArticleRepository repo;
    private final ArticleMapper mapper;
    private final WebClient.Builder webClientBuilder;

    @Override
    public ArticleDto add(ArticleDto dto) {
        StockDto stock = getStockByArticleName(dto.title());
        if (stock == null || stock.quantity() <= 0) {
            throw new IllegalStateException("Insufficient stock for article: " + dto.title());
        }

        decrementStockQuantity(stock);

        Article a = mapper.mapToEntity(dto);
        a.setCreatedAt(LocalDateTime.now());
        return mapper.mapToDto(repo.save(a));
    }

    private StockDto getStockByArticleName(String articleName) {
        try {
            return webClientBuilder.build()
                    .get()
                    .uri("http://localhost:8082/stocks/article/{name}", articleName)
                    .retrieve()
                    .bodyToMono(StockDto.class)
                    .block();
        } catch (Exception e) {
            System.err.println("Failed to fetch stock: " + e.getMessage());
            return null;
        }
    }

    private void decrementStockQuantity(StockDto stock) {
        int newQuantity = stock.quantity() - 1;
        Map<String, Object> updateFields = Map.of("quantity", newQuantity);

        try {
            webClientBuilder.build()
                    .patch()
                    .uri("http://localhost:8082/stocks/{id}", stock.id())
                    .bodyValue(updateFields)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to decrement stock quantity: " + e.getMessage());
        }
    }

    @Override
    public ArticleDto update(Long id, Map<Object, Object> fields) {
        Article a = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Not found"));
        fields.forEach((key, value) -> {
            Field field = ReflectionUtils.findField(Article.class, (String) key);
            if (field != null) {
                field.setAccessible(true);
                ReflectionUtils.setField(field, a, value);
            }
        });
        a.setUpdatedAt(LocalDateTime.now());
        return mapper.mapToDto(repo.save(a));
    }

    @Override
    public boolean delete(Long id) {
        repo.deleteById(id);
        return !repo.existsById(id);
    }

    @Override
    public Page<ArticleDto> getArticles(int page, int size) {
        return repo.findAll(PageRequest.of(page, size)).map(mapper::mapToDto);
    }

    @Override
    public ArticleDto getArticle(Long id) {
        return repo.findById(id).map(mapper::mapToDto).orElseThrow(() -> new IllegalArgumentException("Not found"));
    }

    @Override
    public ArticleDto getArticleByTitle(String title) {
        return repo.findByTitle(title).map(mapper::mapToDto).orElseThrow(() -> new IllegalArgumentException("Not found"));
    }
}
