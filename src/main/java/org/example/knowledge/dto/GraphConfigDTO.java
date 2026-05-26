package org.example.knowledge.dto;

import java.time.LocalDateTime;

public class GraphConfigDTO {

    private String id;
    private String categoryId;
    private String tableId;
    private String tableName;
    private String nodeNameField;
    private String nodeLabelField;
    private Boolean isEnabled;
    private LocalDateTime createdAt;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCategoryId() { return categoryId; }
    public void setCategoryId(String categoryId) { this.categoryId = categoryId; }

    public String getTableId() { return tableId; }
    public void setTableId(String tableId) { this.tableId = tableId; }

    public String getTableName() { return tableName; }
    public void setTableName(String tableName) { this.tableName = tableName; }

    public String getNodeNameField() { return nodeNameField; }
    public void setNodeNameField(String nodeNameField) { this.nodeNameField = nodeNameField; }

    public String getNodeLabelField() { return nodeLabelField; }
    public void setNodeLabelField(String nodeLabelField) { this.nodeLabelField = nodeLabelField; }

    public Boolean getIsEnabled() { return isEnabled; }
    public void setIsEnabled(Boolean isEnabled) { this.isEnabled = isEnabled; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
