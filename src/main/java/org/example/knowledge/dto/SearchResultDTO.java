package org.example.knowledge.dto;

import java.time.LocalDateTime;
import java.util.Map;

public class SearchResultDTO {

    private String dataId;
    private String tableId;
    private String tableName;
    private String categoryId;
    private String categoryName;
    private Map<String, Object> jsonbData;
    private String matchedText;
    private LocalDateTime updatedAt;

    public String getDataId() { return dataId; }
    public void setDataId(String dataId) { this.dataId = dataId; }

    public String getTableId() { return tableId; }
    public void setTableId(String tableId) { this.tableId = tableId; }

    public String getTableName() { return tableName; }
    public void setTableName(String tableName) { this.tableName = tableName; }

    public String getCategoryId() { return categoryId; }
    public void setCategoryId(String categoryId) { this.categoryId = categoryId; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public Map<String, Object> getJsonbData() { return jsonbData; }
    public void setJsonbData(Map<String, Object> jsonbData) { this.jsonbData = jsonbData; }

    public String getMatchedText() { return matchedText; }
    public void setMatchedText(String matchedText) { this.matchedText = matchedText; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
