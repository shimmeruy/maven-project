package org.example.knowledge.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "knowledge_table_graph_config")
public class KnowledgeGraphConfig {

    @Id
    @Column(name = "id", length = 32)
    private String id;

    @Column(name = "category_id", length = 32)
    private String categoryId;

    @Column(name = "table_id", length = 32)
    private String tableId;

    @Column(name = "node_name_field", length = 100, nullable = false)
    private String nodeNameField;

    @Column(name = "node_label_field", length = 100)
    private String nodeLabelField;

    @Column(name = "is_enabled")
    private Boolean isEnabled = true;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (id == null) {
            id = org.example.knowledge.util.IdGenerator.generate();
        }
        createdAt = LocalDateTime.now();
        if (isEnabled == null) isEnabled = true;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCategoryId() { return categoryId; }
    public void setCategoryId(String categoryId) { this.categoryId = categoryId; }

    public String getTableId() { return tableId; }
    public void setTableId(String tableId) { this.tableId = tableId; }

    public String getNodeNameField() { return nodeNameField; }
    public void setNodeNameField(String nodeNameField) { this.nodeNameField = nodeNameField; }

    public String getNodeLabelField() { return nodeLabelField; }
    public void setNodeLabelField(String nodeLabelField) { this.nodeLabelField = nodeLabelField; }

    public Boolean getIsEnabled() { return isEnabled; }
    public void setIsEnabled(Boolean isEnabled) { this.isEnabled = isEnabled; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
