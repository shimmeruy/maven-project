package org.example.knowledge.repository;

import org.example.knowledge.entity.KnowledgeTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KnowledgeTableRepository extends JpaRepository<KnowledgeTable, String> {

    List<KnowledgeTable> findByCategoryIdOrderBySortOrderAsc(String categoryId);

    int countByCategoryId(String categoryId);
}
