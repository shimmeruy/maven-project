package org.example.knowledge.entity;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "knowledge_graph_node")
@SQLDelete(sql = "UPDATE knowledge_graph_node SET is_deleted = true WHERE id = ?")
@Where(clause = "is_deleted = false")
public class KnowledgeGraphNode {

    @Id
    @Column(name = "id", length = 32)
    private String id;

    @Column(name = "name", length = 200, nullable = false)
    private String name;

    @Column(name = "label", length = 100)
    private String label;

    @Column(name = "source_table_id", length = 32)
    private String sourceTableId;

    @Column(name = "source_data_id", length = 32)
    private String sourceDataId;

    @Column(name = "source_field_code", length = 100)
    private String sourceFieldCode;

    @Column(name = "node_type", length = 20, nullable = false)
    private String nodeType;

    @Column(name = "extra_info", columnDefinition = "jsonb")
    @org.hibernate.annotations.ColumnTransformer(write = "?::jsonb")
    private String extraInfo;

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
        if (isDeleted == null) isDeleted = false;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    public String getSourceTableId() { return sourceTableId; }
    public void setSourceTableId(String sourceTableId) { this.sourceTableId = sourceTableId; }

    public String getSourceDataId() { return sourceDataId; }
    public void setSourceDataId(String sourceDataId) { this.sourceDataId = sourceDataId; }

    public String getSourceFieldCode() { return sourceFieldCode; }
    public void setSourceFieldCode(String sourceFieldCode) { this.sourceFieldCode = sourceFieldCode; }

    public String getNodeType() { return nodeType; }
    public void setNodeType(String nodeType) { this.nodeType = nodeType; }

    public String getExtraInfo() { return extraInfo; }
    public void setExtraInfo(String extraInfo) { this.extraInfo = extraInfo; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Boolean getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Boolean isDeleted) { this.isDeleted = isDeleted; }
}
