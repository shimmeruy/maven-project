package org.example.knowledge.controller;

import org.example.knowledge.dto.ApiResponse;
import org.example.knowledge.dto.MapConfigDTO;
import org.example.knowledge.entity.KnowledgeTable;
import org.example.knowledge.service.KnowledgeTableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/knowledge")
public class KnowledgeTableController {

    @Autowired
    private KnowledgeTableService tableService;

    @GetMapping("/categories/{categoryId}/tables")
    public ApiResponse<?> getByCategory(@PathVariable String categoryId) {
        return ApiResponse.success(tableService.getByCategory(categoryId));
    }

    @GetMapping("/tables/{id}")
    public ApiResponse<?> getDetail(@PathVariable String id) {
        return ApiResponse.success(tableService.getDetail(id));
    }

    @PostMapping("/tables")
    public ApiResponse<?> create(@RequestBody KnowledgeTable table) {
        return ApiResponse.success(tableService.create(table));
    }

    @PutMapping("/tables/{id}")
    public ApiResponse<?> update(@PathVariable String id, @RequestBody KnowledgeTable table) {
        return ApiResponse.success(tableService.update(id, table));
    }

    @DeleteMapping("/tables/{id}")
    public ApiResponse<?> delete(@PathVariable String id) {
        tableService.delete(id);
        return ApiResponse.success();
    }

    @PutMapping("/tables/{id}/map-config")
    public ApiResponse<?> updateMapConfig(@PathVariable String id, @RequestBody MapConfigDTO config) {
        tableService.updateMapConfig(id, config);
        return ApiResponse.success();
    }
}
