package org.example.knowledge.entity;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "knowledge_table")
@SQLDelete(sql = "UPDATE knowledge_table SET is_deleted = true WHERE id = ?")
@Where(clause = "is_deleted = false")
public class KnowledgeTable {

    @Id
    @Column(name = "id", length = 32)
    private String id;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "category_id", length = 32)
    private String categoryId;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "sort_order")
    private Integer sortOrder = 0;

    @Column(name = "enable_map")
    private Boolean enableMap = false;

    @Column(name = "map_longitude_field", length = 100)
    private String mapLongitudeField;

    @Column(name = "map_latitude_field", length = 100)
    private String mapLatitudeField;

    @Column(name = "map_label_field", length = 100)
    private String mapLabelField;

    @Column(name = "map_desc_field", length = 100)
    private String mapDescField;

    @Column(name = "data_count")
    private Integer dataCount = 0;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

    @PrePersist
    public void prePersist() {
        if (id == null) {
            id = org.example.knowledge.util.IdGenerator.generate();
        }
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (sortOrder == null) sortOrder = 0;
        if (enableMap == null) enableMap = false;
        if (dataCount == null) dataCount = 0;
        if (isDeleted == null) isDeleted = false;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

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

    public Boolean getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Boolean isDeleted) { this.isDeleted = isDeleted; }
}
