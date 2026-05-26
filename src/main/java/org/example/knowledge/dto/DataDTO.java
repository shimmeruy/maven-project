package org.example.knowledge.dto;

import java.time.LocalDateTime;
import java.util.Map;

public class DataDTO {

    private String id;
    private String tableId;
    private Map<String, Object> jsonbData;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTableId() { return tableId; }
    public void setTableId(String tableId) { this.tableId = tableId; }

    public Map<String, Object> getJsonbData() { return jsonbData; }
    public void setJsonbData(Map<String, Object> jsonbData) { this.jsonbData = jsonbData; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
