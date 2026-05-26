package org.example.knowledge.repository;

import org.example.knowledge.entity.KnowledgeField;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface KnowledgeFieldRepository extends JpaRepository<KnowledgeField, String> {

    List<KnowledgeField> findByTableIdOrderBySortOrderAsc(String tableId);

    Optional<KnowledgeField> findByTableIdAndCode(String tableId, String code);

    int countByTableId(String tableId);

    @Modifying
    @Query("UPDATE KnowledgeField f SET f.sortOrder = :sortOrder WHERE f.id = :id")
    void updateSortOrder(@Param("id") String id, @Param("sortOrder") int sortOrder);
}
