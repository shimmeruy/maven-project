package org.example.knowledge.service;

import org.example.knowledge.dto.GraphConfigDTO;
import org.example.knowledge.dto.GraphEdgeDTO;
import org.example.knowledge.dto.GraphNodeDTO;
import org.example.knowledge.entity.*;
import org.example.knowledge.enums.NodeType;
import org.example.knowledge.repository.*;
import org.example.knowledge.util.JsonbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class GraphService {

    @Autowired
    private KnowledgeGraphConfigRepository configRepository;

    @Autowired
    private KnowledgeGraphNodeRepository nodeRepository;

    @Autowired
    private KnowledgeGraphEdgeRepository edgeRepository;

    @Autowired
    private KnowledgeTableRepository tableRepository;

    @Autowired
    private KnowledgeDataRepository dataRepository;

    @Autowired
    private KnowledgeFieldRepository fieldRepository;

    // ---- Config ----

    public List<GraphConfigDTO> getConfigs() {
        List<KnowledgeGraphConfig> configs = configRepository.findByIsEnabledTrue();
        return configs.stream().map(this::toConfigDTO).collect(Collectors.toList());
    }

    @Transactional
    public KnowledgeGraphConfig createConfig(KnowledgeGraphConfig config) {
        KnowledgeGraphConfig saved = configRepository.save(config);
        syncNodesFromConfig(saved);
        return saved;
    }

    @Transactional
    public KnowledgeGraphConfig updateConfig(String id, KnowledgeGraphConfig updated) {
        KnowledgeGraphConfig existing = configRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Graph config not found: " + id));
        if (updated.getNodeNameField() != null) existing.setNodeNameField(updated.getNodeNameField());
        if (updated.getNodeLabelField() != null) existing.setNodeLabelField(updated.getNodeLabelField());
        if (updated.getIsEnabled() != null) existing.setIsEnabled(updated.getIsEnabled());
        return configRepository.save(existing);
    }

    @Transactional
    public void deleteConfig(String id) {
        KnowledgeGraphConfig config = configRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Graph config not found: " + id));
        List<KnowledgeGraphNode> nodes = nodeRepository.findBySourceTableId(config.getTableId());
        for (KnowledgeGraphNode node : nodes) {
            List<KnowledgeGraphEdge> edges = edgeRepository.findByNodeId(node.getId());
            edgeRepository.deleteAll(edges);
        }
        nodeRepository.deleteAll(nodes);
        configRepository.delete(config);
    }

    // ---- Nodes ----

    public List<GraphNodeDTO> getNodes(String sourceTableId) {
        List<KnowledgeGraphNode> nodes;
        if (sourceTableId != null && !sourceTableId.isEmpty()) {
            nodes = nodeRepository.findBySourceTableId(sourceTableId);
        } else {
            nodes = nodeRepository.findAll();
        }
        return nodes.stream().map(this::toNodeDTO).collect(Collectors.toList());
    }

    public GraphNodeDTO getNodeDetail(String id) {
        return toNodeDTO(nodeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Node not found: " + id)));
    }

    // ---- Edges ----

    public List<GraphEdgeDTO> getEdges() {
        List<KnowledgeGraphEdge> edges = edgeRepository.findAll();
        Map<String, String> nodeNameMap = buildNodeNameMap();
        return edges.stream().map(e -> {
            GraphEdgeDTO dto = toEdgeDTO(e);
            dto.setSourceNodeName(nodeNameMap.get(e.getSourceNodeId()));
            dto.setTargetNodeName(nodeNameMap.get(e.getTargetNodeId()));
            return dto;
        }).collect(Collectors.toList());
    }

    @Transactional
    public KnowledgeGraphEdge createEdge(KnowledgeGraphEdge edge) {
        nodeRepository.findById(edge.getSourceNodeId())
                .orElseThrow(() -> new RuntimeException("Source node not found: " + edge.getSourceNodeId()));
        nodeRepository.findById(edge.getTargetNodeId())
                .orElseThrow(() -> new RuntimeException("Target node not found: " + edge.getTargetNodeId()));
        return edgeRepository.save(edge);
    }

    @Transactional
    public KnowledgeGraphEdge updateEdge(String id, KnowledgeGraphEdge updated) {
        KnowledgeGraphEdge existing = edgeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Edge not found: " + id));
        if (updated.getRelationName() != null) existing.setRelationName(updated.getRelationName());
        if (updated.getRelationType() != null) existing.setRelationType(updated.getRelationType());
        return edgeRepository.save(existing);
    }

    @Transactional
    public void deleteEdge(String id) {
        edgeRepository.deleteById(id);
    }

    // ---- Full Graph ----

    public Map<String, Object> getFullGraph() {
        List<KnowledgeGraphNode> nodes = nodeRepository.findAll();
        List<KnowledgeGraphEdge> edges = edgeRepository.findAll();
        Map<String, String> nodeNameMap = nodes.stream()
                .collect(Collectors.toMap(KnowledgeGraphNode::getId, KnowledgeGraphNode::getName));

        List<GraphNodeDTO> nodeDTOs = nodes.stream().map(this::toNodeDTO).collect(Collectors.toList());
        List<GraphEdgeDTO> edgeDTOs = edges.stream().map(e -> {
            GraphEdgeDTO dto = toEdgeDTO(e);
            dto.setSourceNodeName(nodeNameMap.get(e.getSourceNodeId()));
            dto.setTargetNodeName(nodeNameMap.get(e.getTargetNodeId()));
            return dto;
        }).collect(Collectors.toList());

        Map<String, Object> graph = new HashMap<>();
        graph.put("nodes", nodeDTOs);
        graph.put("edges", edgeDTOs);
        return graph;
    }

    // ---- Sync ----

    @Transactional
    public void syncNodesFromConfig(KnowledgeGraphConfig config) {
        List<KnowledgeData> dataList = dataRepository.findByTableId(config.getTableId());
        for (KnowledgeData data : dataList) {
            Map<String, Object> dataMap = JsonbUtil.toMap(data.getJsonbData());
            Object nameValue = dataMap.get(config.getNodeNameField());
            if (nameValue == null) continue;
            String nodeName = String.valueOf(nameValue);
            List<KnowledgeGraphNode> existing = nodeRepository
                    .findBySourceTableIdAndSourceDataId(config.getTableId(), data.getId());
            if (existing.isEmpty()) {
                KnowledgeGraphNode node = new KnowledgeGraphNode();
                node.setName(nodeName);
                node.setSourceTableId(config.getTableId());
                node.setSourceDataId(data.getId());
                node.setSourceFieldCode(config.getNodeNameField());
                node.setNodeType(NodeType.AUTO.name());
                if (config.getNodeLabelField() != null) {
                    Object labelValue = dataMap.get(config.getNodeLabelField());
                    if (labelValue != null) {
                        node.setLabel(String.valueOf(labelValue));
                    }
                }
                nodeRepository.save(node);
            } else {
                KnowledgeGraphNode node = existing.get(0);
                node.setName(nodeName);
                if (config.getNodeLabelField() != null) {
                    Object labelValue = dataMap.get(config.getNodeLabelField());
                    if (labelValue != null) {
                        node.setLabel(String.valueOf(labelValue));
                    }
                }
                nodeRepository.save(node);
            }
        }
    }

    // ---- private helpers ----

    private Map<String, String> buildNodeNameMap() {
        return nodeRepository.findAll().stream()
                .collect(Collectors.toMap(KnowledgeGraphNode::getId, KnowledgeGraphNode::getName));
    }

    private GraphConfigDTO toConfigDTO(KnowledgeGraphConfig entity) {
        GraphConfigDTO dto = new GraphConfigDTO();
        dto.setId(entity.getId());
        dto.setCategoryId(entity.getCategoryId());
        dto.setTableId(entity.getTableId());
        dto.setNodeNameField(entity.getNodeNameField());
        dto.setNodeLabelField(entity.getNodeLabelField());
        dto.setIsEnabled(entity.getIsEnabled());
        dto.setCreatedAt(entity.getCreatedAt());
        tableRepository.findById(entity.getTableId())
                .ifPresent(t -> dto.setTableName(t.getName()));
        return dto;
    }

    private GraphNodeDTO toNodeDTO(KnowledgeGraphNode entity) {
        GraphNodeDTO dto = new GraphNodeDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setLabel(entity.getLabel());
        dto.setSourceTableId(entity.getSourceTableId());
        dto.setSourceDataId(entity.getSourceDataId());
        dto.setSourceFieldCode(entity.getSourceFieldCode());
        dto.setNodeType(entity.getNodeType());
        if (entity.getExtraInfo() != null) {
            dto.setExtraInfo(JsonbUtil.toMap(entity.getExtraInfo()));
        }
        dto.setCreatedAt(entity.getCreatedAt());
        if (entity.getSourceTableId() != null) {
            tableRepository.findById(entity.getSourceTableId())
                    .ifPresent(t -> dto.setSourceTableName(t.getName()));
        }
        return dto;
    }

    private GraphEdgeDTO toEdgeDTO(KnowledgeGraphEdge entity) {
        GraphEdgeDTO dto = new GraphEdgeDTO();
        dto.setId(entity.getId());
        dto.setSourceNodeId(entity.getSourceNodeId());
        dto.setTargetNodeId(entity.getTargetNodeId());
        dto.setRelationName(entity.getRelationName());
        dto.setRelationType(entity.getRelationType());
        if (entity.getExtraInfo() != null) {
            dto.setExtraInfo(JsonbUtil.toMap(entity.getExtraInfo()));
        }
        dto.setCreatedAt(entity.getCreatedAt());
        return dto;
    }
}
