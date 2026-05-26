package org.example.knowledge.controller;

import org.example.knowledge.dto.ApiResponse;
import org.example.knowledge.service.KnowledgeDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/knowledge")
public class KnowledgeDataController {

    @Autowired
    private KnowledgeDataService dataService;

    @GetMapping("/tables/{tableId}/data")
    public ApiResponse<?> getPage(@PathVariable String tableId,
                                  @RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "20") int size) {
        return ApiResponse.success(dataService.getPage(tableId, page, size));
    }

    @GetMapping("/data/{id}")
    public ApiResponse<?> getDetail(@PathVariable String id) {
        return ApiResponse.success(dataService.getDetail(id));
    }

    @PostMapping("/tables/{tableId}/data")
    public ApiResponse<?> create(@PathVariable String tableId, @RequestBody Map<String, Object> data) {
        return ApiResponse.success(dataService.create(tableId, data));
    }

    @PutMapping("/data/{id}")
    public ApiResponse<?> update(@PathVariable String id, @RequestBody Map<String, Object> data) {
        return ApiResponse.success(dataService.update(id, data));
    }

    @DeleteMapping("/data/{id}")
    public ApiResponse<?> delete(@PathVariable String id) {
        dataService.delete(id);
        return ApiResponse.success();
    }

    @DeleteMapping("/tables/{tableId}/data/batch")
    public ApiResponse<?> batchDelete(@PathVariable String tableId, @RequestBody List<String> ids) {
        dataService.batchDelete(tableId, ids);
        return ApiResponse.success();
    }

    @GetMapping("/data/search")
    public ApiResponse<?> search(@RequestParam String keyword,
                                 @RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "20") int size) {
        return ApiResponse.success(dataService.search(keyword, page, size));
    }
}
