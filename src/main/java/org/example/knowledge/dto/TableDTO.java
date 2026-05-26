package org.example.knowledge.dto;

import java.time.LocalDateTime;
import java.util.List;

public class TableDTO {

    private String id;
    private String name;
    private String categoryId;
    private String description;
    private Integer sortOrder;
    private Boolean enableMap;
    private String mapLongitudeField;
    private String mapLatitudeField;
    private String mapLabelField;
    private String mapDescField;
    private Integer dataCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<FieldDTO> fields;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategoryId() { return categoryId; }
    public void setCategoryId(String categoryId) { this.categoryId = categoryId; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }

    public Boolean getEnableMap() { return enableMap; }
    public void setEnableMap(Boolean enableMap) { this.enableMap = enableMap; }

    public String getMapLongitudeField() { return mapLongitudeField; }
    public void setMapLongitudeField(String mapLongitudeField) { this.mapLongitudeField = mapLongitudeField; }

    public String getMapLatitudeField() { return mapLatitudeField; }
    public void setMapLatitudeField(String mapLatitudeField) { this.mapLatitudeField = mapLatitudeField; }

    public String getMapLabelField() { return mapLabelField; }
    public void setMapLabelField(String mapLabelField) { this.mapLabelField = mapLabelField; }

    public String getMapDescField() { return mapDescField; }
    public void setMapDescField(String mapDescField) { this.mapDescField = mapDescField; }

    public Integer getDataCount() { return dataCount; }
    public void setDataCount(Integer dataCount) { this.dataCount = dataCount; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public List<FieldDTO> getFields() { return fields; }
    public void setFields(List<FieldDTO> fields) { this.fields = fields; }
}
