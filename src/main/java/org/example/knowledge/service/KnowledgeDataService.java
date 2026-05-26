package org.example.knowledge.service;

import org.example.knowledge.dto.DataDTO;
import org.example.knowledge.dto.PageResponse;
import org.example.knowledge.dto.SearchResultDTO;
import org.example.knowledge.entity.KnowledgeData;
import org.example.knowledge.entity.KnowledgeField;
import org.example.knowledge.entity.KnowledgeTable;
import org.example.knowledge.enums.FieldType;
import org.example.knowledge.repository.KnowledgeDataRepository;
import org.example.knowledge.repository.KnowledgeFieldRepository;
import org.example.knowledge.repository.KnowledgeTableRepository;
import org.example.knowledge.util.JsonbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class KnowledgeDataService {

    @Autowired
    private KnowledgeDataRepository dataRepository;

    @Autowired
    private KnowledgeFieldRepository fieldRepository;

    @Autowired
    private KnowledgeTableRepository tableRepository;

    @Autowired
    private MinioService minioService;

    public PageResponse<DataDTO> getPage(String tableId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "updatedAt"));
        Page<KnowledgeData> dataPage = dataRepository.findByTableIdOrderByUpdatedAtDesc(tableId, pageable);
        List<DataDTO> content = dataPage.getContent().stream().map(this::toDTO).collect(Collectors.toList());
        return new PageResponse<>(content, dataPage.getTotalElements(),
                dataPage.getTotalPages(), page, size);
    }

    public DataDTO getDetail(String id) {
        return toDTO(getById(id));
    }

    @Transactional
    public KnowledgeData create(String tableId, Map<String, Object> data) {
        KnowledgeTable table = tableRepository.findById(tableId)
                .orElseThrow(() -> new RuntimeException("Table not found: " + tableId));
        List<KnowledgeField> fields = fieldRepository.findByTableIdOrderBySortOrderAsc(tableId);
        validateRequired(data, fields);
        KnowledgeData entity = new KnowledgeData();
        entity.setTableId(tableId);
        entity.setJsonbData(JsonbUtil.toJson(data));
        return dataRepository.save(entity);
    }

    @Transactional
    public KnowledgeData update(String id, Map<String, Object> data) {
        KnowledgeData existing = getById(id);
        List<KnowledgeField> fields = fieldRepository.findByTableIdOrderBySortOrderAsc(existing.getTableId());
        validateRequired(data, fields);
        existing.setJsonbData(JsonbUtil.toJson(data));
        return dataRepository.save(existing);
    }

    @Transactional
    public void delete(String id) {
        KnowledgeData data = getById(id);
        deleteAssociatedImages(data);
        dataRepository.deleteById(id);
    }

    @Transactional
    public void batchDelete(String tableId, List<String> ids) {
        for (String id : ids) {
            delete(id);
        }
    }

    public PageResponse<SearchResultDTO> search(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<KnowledgeData> dataPage = dataRepository.searchAll(keyword, pageable);

        Set<String> tableIds = dataPage.getContent().stream()
                .map(KnowledgeData::getTableId).collect(Collectors.toSet());
        Map<String, KnowledgeTable> tableMap = tableRepository.findAllById(tableIds).stream()
                .collect(Collectors.toMap(KnowledgeTable::getId, t -> t));

        List<SearchResultDTO> results = dataPage.getContent().stream().map(d -> {
            SearchResultDTO dto = new SearchResultDTO();
            dto.setDataId(d.getId());
            dto.setTableId(d.getTableId());
            dto.setJsonbData(JsonbUtil.toMap(d.getJsonbData()));
            dto.setUpdatedAt(d.getUpdatedAt());
            KnowledgeTable table = tableMap.get(d.getTableId());
            if (table != null) {
                dto.setTableName(table.getName());
                dto.setCategoryId(table.getCategoryId());
            }
            return dto;
        }).collect(Collectors.toList());

        return new PageResponse<>(results, dataPage.getTotalElements(),
                dataPage.getTotalPages(), page, size);
    }

    public KnowledgeData getById(String id) {
        return dataRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Data not found: " + id));
    }

    private void validateRequired(Map<String, Object> data, List<KnowledgeField> fields) {
        for (KnowledgeField field : fields) {
            if (Boolean.TRUE.equals(field.getIsRequired())) {
                Object value = data.get(field.getCode());
                if (value == null || (value instanceof String && ((String) value).isEmpty())) {
                    throw new RuntimeException("Field '" + field.getName() + "' is required");
                }
            }
        }
    }

    private void deleteAssociatedImages(KnowledgeData data) {
        List<KnowledgeField> fields = fieldRepository.findByTableIdOrderBySortOrderAsc(data.getTableId());
        Map<String, Object> dataMap = JsonbUtil.toMap(data.getJsonbData());
        for (KnowledgeField field : fields) {
            if (FieldType.IMAGE.name().equals(field.getFieldType())) {
                Object imgPath = dataMap.get(field.getCode());
                if (imgPath instanceof String && !((String) imgPath).isEmpty()) {
                    minioService.delete((String) imgPath);
                }
            }
        }
    }

    private DataDTO toDTO(KnowledgeData entity) {
        DataDTO dto = new DataDTO();
        dto.setId(entity.getId());
        dto.setTableId(entity.getTableId());
        dto.setJsonbData(JsonbUtil.toMap(entity.getJsonbData()));
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}
