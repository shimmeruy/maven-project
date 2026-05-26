package org.example.knowledge.repository;

import org.example.knowledge.entity.KnowledgeGraphEdge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KnowledgeGraphEdgeRepository extends JpaRepository<KnowledgeGraphEdge, String> {

    List<KnowledgeGraphEdge> findBySourceNodeId(String sourceNodeId);

    List<KnowledgeGraphEdge> findByTargetNodeId(String targetNodeId);

    @Query("SELECT e FROM KnowledgeGraphEdge e WHERE e.sourceNodeId = :nodeId OR e.targetNodeId = :nodeId")
    List<KnowledgeGraphEdge> findByNodeId(@Param("nodeId") String nodeId);
}
