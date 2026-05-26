package org.example.knowledge.dto;

import java.time.LocalDateTime;
import java.util.Map;

public class GraphNodeDTO {

    private String id;
    private String name;
    private String label;
    private String sourceTableId;
    private String sourceTableName;
    private String sourceDataId;
    private String sourceFieldCode;
    private String nodeType;
    private Map<String, Object> extraInfo;
    private LocalDateTime createdAt;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    public String getSourceTableId() { return sourceTableId; }
    public void setSourceTableId(String sourceTableId) { this.sourceTableId = sourceTableId; }

    public String getSourceTableName() { return sourceTableName; }
    public void setSourceTableName(String sourceTableName) { this.sourceTableName = sourceTableName; }

    public String getSourceDataId() { return sourceDataId; }
    public void setSourceDataId(String sourceDataId) { this.sourceDataId = sourceDataId; }

    public String getSourceFieldCode() { return sourceFieldCode; }
    public void setSourceFieldCode(String sourceFieldCode) { this.sourceFieldCode = sourceFieldCode; }

    public String getNodeType() { return nodeType; }
    public void setNodeType(String nodeType) { this.nodeType = nodeType; }

    public Map<String, Object> getExtraInfo() { return extraInfo; }
    public void setExtraInfo(Map<String, Object> extraInfo) { this.extraInfo = extraInfo; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
