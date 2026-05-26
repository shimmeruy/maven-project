package org.example.knowledge.entity;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "knowledge_field")
@SQLDelete(sql = "UPDATE knowledge_field SET is_deleted = true WHERE id = ?")
@Where(clause = "is_deleted = false")
public class KnowledgeField {

    @Id
    @Column(name = "id", length = 32)
    private String id;

    @Column(name = "table_id", length = 32)
    private String tableId;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "code", length = 100, nullable = false)
    private String code;

    @Column(name = "field_type", length = 20, nullable = false)
    private String fieldType;

    @Column(name = "is_required")
    private Boolean isRequired = false;

    @Column(name = "sort_order")
    private Integer sortOrder = 0;

    @Column(name = "default_value", length = 500)
    private String defaultValue;

    @Column(name = "field_config", columnDefinition = "jsonb")
    @org.hibernate.annotations.ColumnTransformer(write = "?::jsonb")
    private String fieldConfig;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

    @PrePersist
    public void prePersist() {
        if (id == null) {
            id = org.example.knowledge.util.IdGenerator.generate();
        }
        createdAt = LocalDateTime.now();
        if (isRequired == null) isRequired = false;
        if (sortOrder == null) sortOrder = 0;
        if (isDeleted == null) isDeleted = false;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTableId() { return tableId; }
    public void setTableId(String tableId) { this.tableId = tableId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getFieldType() { return fieldType; }
    public void setFieldType(String fieldType) { this.fieldType = fieldType; }

    public Boolean getIsRequired() { return isRequired; }
    public void setIsRequired(Boolean isRequired) { this.isRequired = isRequired; }

    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }

    public String getDefaultValue() { return defaultValue; }
    public void setDefaultValue(String defaultValue) { this.defaultValue = defaultValue; }

    public String getFieldConfig() { return fieldConfig; }
    public void setFieldConfig(String fieldConfig) { this.fieldConfig = fieldConfig; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Boolean getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Boolean isDeleted) { this.isDeleted = isDeleted; }
}
