package org.example.knowledge.entity;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "knowledge_data")
@SQLDelete(sql = "UPDATE knowledge_data SET is_deleted = true WHERE id = ?")
@Where(clause = "is_deleted = false")
public class KnowledgeData {

    @Id
    @Column(name = "id", length = 32)
    private String id;

    @Column(name = "table_id", length = 32)
    private String tableId;

    @Column(name = "jsonb_data", columnDefinition = "jsonb", nullable = false)
    @org.hibernate.annotations.ColumnTransformer(write = "?::jsonb")
    private String jsonbData;

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
        if (isDeleted == null) isDeleted = false;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTableId() { return tableId; }
    public void setTableId(String tableId) { this.tableId = tableId; }

    public String getJsonbData() { return jsonbData; }
    public void setJsonbData(String jsonbData) { this.jsonbData = jsonbData; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Boolean getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Boolean isDeleted) { this.isDeleted = isDeleted; }
}
