package org.example.knowledge.repository;

import org.example.knowledge.entity.KnowledgeGraphConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface KnowledgeGraphConfigRepository extends JpaRepository<KnowledgeGraphConfig, String> {

    List<KnowledgeGraphConfig> findByCategoryId(String categoryId);

    Optional<KnowledgeGraphConfig> findByTableId(String tableId);

    List<KnowledgeGraphConfig> findByIsEnabledTrue();
}
