package org.example.knowledge.controller;

import org.example.knowledge.dto.ApiResponse;
import org.example.knowledge.entity.KnowledgeField;
import org.example.knowledge.service.KnowledgeFieldService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/knowledge")
public class KnowledgeFieldController {

    @Autowired
    private KnowledgeFieldService fieldService;

    @GetMapping("/tables/{tableId}/fields")
    public ApiResponse<?> getByTableId(@PathVariable String tableId) {
        return ApiResponse.success(fieldService.getByTableId(tableId));
    }

    @PostMapping("/tables/{tableId}/fields")
    public ApiResponse<?> create(@PathVariable String tableId, @RequestBody KnowledgeField field) {
        return ApiResponse.success(fieldService.create(tableId, field));
    }

    @PutMapping("/fields/{id}")
    public ApiResponse<?> update(@PathVariable String id, @RequestBody KnowledgeField field) {
        return ApiResponse.success(fieldService.update(id, field));
    }

    @DeleteMapping("/fields/{id}")
    public ApiResponse<?> delete(@PathVariable String id) {
        fieldService.delete(id);
        return ApiResponse.success();
    }

    @PutMapping("/fields/sort")
    public ApiResponse<?> batchUpdateSort(@RequestBody List<KnowledgeField> sortedFields) {
        fieldService.batchUpdateSort(sortedFields);
        return ApiResponse.success();
    }
}
