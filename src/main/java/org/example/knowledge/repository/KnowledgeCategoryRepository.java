package org.example.knowledge.repository;

import org.example.knowledge.entity.KnowledgeCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KnowledgeCategoryRepository extends JpaRepository<KnowledgeCategory, String> {

    List<KnowledgeCategory> findByParentIdOrderBySortOrderAsc(String parentId);

    List<KnowledgeCategory> findByParentIdIsNullOrderBySortOrderAsc();

    List<KnowledgeCategory> findAllByOrderBySortOrderAsc();

    int countByParentId(String parentId);
}
