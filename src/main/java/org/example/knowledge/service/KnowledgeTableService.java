package org.example.knowledge.service;

import org.example.knowledge.dto.FieldDTO;
import org.example.knowledge.dto.MapConfigDTO;
import org.example.knowledge.dto.TableDTO;
import org.example.knowledge.entity.KnowledgeField;
import org.example.knowledge.entity.KnowledgeTable;
import org.example.knowledge.repository.KnowledgeDataRepository;
import org.example.knowledge.repository.KnowledgeFieldRepository;
import org.example.knowledge.repository.KnowledgeTableRepository;
import org.example.knowledge.util.JsonbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class KnowledgeTableService {

    @Autowired
    private KnowledgeTableRepository tableRepository;

    @Autowired
    private KnowledgeFieldRepository fieldRepository;

    @Autowired
    private KnowledgeDataRepository dataRepository;

    public List<TableDTO> getByCategory(String categoryId) {
        List<KnowledgeTable> tables = tableRepository.findByCategoryIdOrderBySortOrderAsc(categoryId);
        return tables.stream().map(t -> {
            int count = dataRepository.countByTableId(t.getId());
            t.setDataCount(count);
            return toDTO(t);
        }).collect(Collectors.toList());
    }

    public TableDTO getDetail(String id) {
        KnowledgeTable table = getById(id);
        int count = dataRepository.countByTableId(id);
        table.setDataCount(count);
        TableDTO dto = toDTO(table);
        List<KnowledgeField> fields = fieldRepository.findByTableIdOrderBySortOrderAsc(id);
        dto.setFields(fields.stream().map(this::fieldToDTO).collect(Collectors.toList()));
        return dto;
    }

    @Transactional
    public KnowledgeTable create(KnowledgeTable table) {
        return tableRepository.save(table);
    }

    @Transactional
    public KnowledgeTable update(String id, KnowledgeTable updated) {
        KnowledgeTable existing = getById(id);
        if (updated.getName() != null) existing.setName(updated.getName());
        if (updated.getDescription() != null) existing.setDescription(updated.getDescription());
        if (updated.getSortOrder() != null) existing.setSortOrder(updated.getSortOrder());
        return tableRepository.save(existing);
    }

    @Transactional
    public void delete(String id) {
        getById(id);
        tableRepository.deleteById(id);
    }

    @Transactional
    public void updateMapConfig(String id, MapConfigDTO config) {
        KnowledgeTable table = getById(id);
        table.setEnableMap(config.getEnableMap());
        table.setMapLongitudeField(config.getMapLongitudeField());
        table.setMapLatitudeField(config.getMapLatitudeField());
        table.setMapLabelField(config.getMapLabelField());
        table.setMapDescField(config.getMapDescField());
        tableRepository.save(table);
    }

    public KnowledgeTable getById(String id) {
        return tableRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Table not found: " + id));
    }

    private TableDTO toDTO(KnowledgeTable entity) {
        TableDTO dto = new TableDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setCategoryId(entity.getCategoryId());
        dto.setDescription(entity.getDescription());
        dto.setSortOrder(entity.getSortOrder());
        dto.setEnableMap(entity.getEnableMap());
        dto.setMapLongitudeField(entity.getMapLongitudeField());
        dto.setMapLatitudeField(entity.getMapLatitudeField());
        dto.setMapLabelField(entity.getMapLabelField());
        dto.setMapDescField(entity.getMapDescField());
        dto.setDataCount(entity.getDataCount());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }

    private FieldDTO fieldToDTO(KnowledgeField entity) {
        FieldDTO dto = new FieldDTO();
        dto.setId(entity.getId());
        dto.setTableId(entity.getTableId());
        dto.setName(entity.getName());
        dto.setCode(entity.getCode());
        dto.setFieldType(entity.getFieldType());
        dto.setIsRequired(entity.getIsRequired());
        dto.setSortOrder(entity.getSortOrder());
        dto.setDefaultValue(entity.getDefaultValue());
        if (entity.getFieldConfig() != null) {
            dto.setFieldConfig(JsonbUtil.toMap(entity.getFieldConfig()));
        }
        dto.setCreatedAt(entity.getCreatedAt());
        return dto;
    }
}
