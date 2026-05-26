package org.example.knowledge.repository;

import org.example.knowledge.entity.KnowledgeData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KnowledgeDataRepository extends JpaRepository<KnowledgeData, String> {

    Page<KnowledgeData> findByTableIdOrderByUpdatedAtDesc(String tableId, Pageable pageable);

    int countByTableId(String tableId);

    List<KnowledgeData> findByTableId(String tableId);

    @Query(value = "SELECT * FROM knowledge_data WHERE jsonb_data::text ILIKE '%' || :keyword || '%' AND is_deleted = false ORDER BY updated_at DESC",
            countQuery = "SELECT count(*) FROM knowledge_data WHERE jsonb_data::text ILIKE '%' || :keyword || '%' AND is_deleted = false",
            nativeQuery = true)
    Page<KnowledgeData> searchAll(@Param("keyword") String keyword, Pageable pageable);

    @Query(value = "SELECT DISTINCT table_id FROM knowledge_data WHERE jsonb_data::text ILIKE '%' || :keyword || '%' AND is_deleted = false",
            nativeQuery = true)
    List<String> findTableIdsByKeyword(@Param("keyword") String keyword);
}
