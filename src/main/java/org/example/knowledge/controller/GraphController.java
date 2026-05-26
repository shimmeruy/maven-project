package org.example.knowledge.controller;

import org.example.knowledge.dto.ApiResponse;
import org.example.knowledge.entity.KnowledgeGraphConfig;
import org.example.knowledge.entity.KnowledgeGraphEdge;
import org.example.knowledge.service.GraphService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/knowledge/graph")
public class GraphController {

    @Autowired
    private GraphService graphService;

    @GetMapping("/configs")
    public ApiResponse<?> getConfigs() {
        return ApiResponse.success(graphService.getConfigs());
    }

    @PostMapping("/configs")
    public ApiResponse<?> createConfig(@RequestBody KnowledgeGraphConfig config) {
        return ApiResponse.success(graphService.createConfig(config));
    }

    @PutMapping("/configs/{id}")
    public ApiResponse<?> updateConfig(@PathVariable String id, @RequestBody KnowledgeGraphConfig config) {
        return ApiResponse.success(graphService.updateConfig(id, config));
    }

    @DeleteMapping("/configs/{id}")
    public ApiResponse<?> deleteConfig(@PathVariable String id) {
        graphService.deleteConfig(id);
        return ApiResponse.success();
    }

    @GetMapping("/nodes")
    public ApiResponse<?> getNodes(@RequestParam(required = false) String sourceTableId) {
        return ApiResponse.success(graphService.getNodes(sourceTableId));
    }

    @GetMapping("/nodes/{id}")
    public ApiResponse<?> getNodeDetail(@PathVariable String id) {
        return ApiResponse.success(graphService.getNodeDetail(id));
    }

    @GetMapping("/edges")
    public ApiResponse<?> getEdges() {
        return ApiResponse.success(graphService.getEdges());
    }

    @PostMapping("/edges")
    public ApiResponse<?> createEdge(@RequestBody KnowledgeGraphEdge edge) {
        return ApiResponse.success(graphService.createEdge(edge));
    }

    @PutMapping("/edges/{id}")
    public ApiResponse<?> updateEdge(@PathVariable String id, @RequestBody KnowledgeGraphEdge edge) {
        return ApiResponse.success(graphService.updateEdge(id, edge));
    }

    @DeleteMapping("/edges/{id}")
    public ApiResponse<?> deleteEdge(@PathVariable String id) {
        graphService.deleteEdge(id);
        return ApiResponse.success();
    }

    @GetMapping("/full")
    public ApiResponse<?> getFullGraph() {
        return ApiResponse.success(graphService.getFullGraph());
    }
}
