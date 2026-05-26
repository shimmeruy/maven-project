package org.example.knowledge.controller;

import org.example.knowledge.dto.ApiResponse;
import org.example.knowledge.entity.KnowledgeCategory;
import org.example.knowledge.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/knowledge/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/tree")
    public ApiResponse<?> getTree() {
        return ApiResponse.success(categoryService.getTree());
    }

    @GetMapping("/{id}")
    public ApiResponse<?> getDetail(@PathVariable String id) {
        return ApiResponse.success(categoryService.getDetail(id));
    }

    @PostMapping
    public ApiResponse<?> create(@RequestBody KnowledgeCategory category) {
        return ApiResponse.success(categoryService.create(category));
    }

    @PutMapping("/{id}")
    public ApiResponse<?> update(@PathVariable String id, @RequestBody KnowledgeCategory category) {
        return ApiResponse.success(categoryService.update(id, category));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<?> delete(@PathVariable String id) {
        categoryService.delete(id);
        return ApiResponse.success();
    }

    @PutMapping("/{id}/sort")
    public ApiResponse<?> updateSort(@PathVariable String id, @RequestParam Integer sortOrder) {
        categoryService.updateSort(id, sortOrder);
        return ApiResponse.success();
    }
}
