package org.example.knowledge.service;

import org.example.knowledge.dto.FieldDTO;
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
import java.util.stream.Collectors;

@Service
public class KnowledgeFieldService {

    @Autowired
    private KnowledgeFieldRepository fieldRepository;

    @Autowired
    private KnowledgeTableRepository tableRepository;

    @Autowired
    private KnowledgeDataRepository dataRepository;

    public List<FieldDTO> getByTableId(String tableId) {
        List<KnowledgeField> fields = fieldRepository.findByTableIdOrderBySortOrderAsc(tableId);
        return fields.stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional
    public KnowledgeField create(String tableId, KnowledgeField field) {
        KnowledgeTable table = tableRepository.findById(tableId)
                .orElseThrow(() -> new RuntimeException("Table not found: " + tableId));
        field.setTableId(tableId);
        fieldRepository.findByTableIdAndCode(tableId, field.getCode()).ifPresent(f -> {
            throw new RuntimeException("Field code already exists: " + field.getCode());
        });
        return fieldRepository.save(field);
    }

    @Transactional
    public KnowledgeField update(String id, KnowledgeField updated) {
        KnowledgeField existing = getById(id);
        if (updated.getFieldConfig() != null) {
            existing.setFieldConfig(updated.getFieldConfig());
        }
        if (updated.getIsRequired() != null) {
            existing.setIsRequired(updated.getIsRequired());
        }
        if (updated.getSortOrder() != null) {
            existing.setSortOrder(updated.getSortOrder());
        }
        if (updated.getDefaultValue() != null) {
            existing.setDefaultValue(updated.getDefaultValue());
        }
        return fieldRepository.save(existing);
    }

    @Transactional
    public void delete(String id) {
        KnowledgeField field = getById(id);
        fieldRepository.deleteById(id);
    }

    @Transactional
    public void batchUpdateSort(List<KnowledgeField> sortedFields) {
        for (KnowledgeField f : sortedFields) {
            fieldRepository.updateSortOrder(f.getId(), f.getSortOrder());
        }
    }

    public KnowledgeField getById(String id) {
        return fieldRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Field not found: " + id));
    }

    private FieldDTO toDTO(KnowledgeField entity) {
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
