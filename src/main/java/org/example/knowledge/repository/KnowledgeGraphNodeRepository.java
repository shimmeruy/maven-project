package org.example.knowledge.repository;

import org.example.knowledge.entity.KnowledgeGraphNode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KnowledgeGraphNodeRepository extends JpaRepository<KnowledgeGraphNode, String> {

    List<KnowledgeGraphNode> findBySourceTableId(String sourceTableId);

    List<KnowledgeGraphNode> findBySourceTableIdAndSourceDataId(String sourceTableId, String sourceDataId);

    List<KnowledgeGraphNode> findByNodeType(String nodeType);
}
