package com.esprit.articleservice.controller;

import com.esprit.articleservice.service.IArticleService;
import com.esprit.shared.dto.ArticleDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/articles")
@RequiredArgsConstructor
public class ArticleRestController {

    private final IArticleService service;

    @PostMapping
    public ArticleDto add(@RequestBody ArticleDto dto) {
        return service.add(dto);
    }

    @PatchMapping("{id}")
    public ArticleDto update(@PathVariable Long id, @RequestBody Map<Object, Object> fields) {
        return service.update(id, fields);
    }

    @DeleteMapping("{id}")
    public boolean delete(@PathVariable Long id) {
        return service.delete(id);
    }

    @GetMapping
    public Page<ArticleDto> getAll(@RequestParam int page, @RequestParam int size) {
        return service.getArticles(page, size);
    }

    @GetMapping("{id}")
    public ArticleDto getById(@PathVariable Long id) {
        return service.getArticle(id);
    }

    @GetMapping("/title/{title}")
    public ArticleDto getByTitle(@PathVariable String title) {
        return service.getArticleByTitle(title);
    }
}
