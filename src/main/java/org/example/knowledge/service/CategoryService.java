package org.example.knowledge.service;

import org.example.knowledge.dto.CategoryDTO;
import org.example.knowledge.entity.KnowledgeCategory;
import org.example.knowledge.repository.KnowledgeCategoryRepository;
import org.example.knowledge.repository.KnowledgeTableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    @Autowired
    private KnowledgeCategoryRepository categoryRepository;

    @Autowired
    private KnowledgeTableRepository tableRepository;

    public List<CategoryDTO> getTree() {
        List<KnowledgeCategory> all = categoryRepository.findAllByOrderBySortOrderAsc();
        Map<String, List<KnowledgeCategory>> parentMap = all.stream()
                .collect(Collectors.groupingBy(c ->
                        c.getParentId() == null ? "" : c.getParentId()));

        return buildTree(parentMap.get("") == null ? new ArrayList<>() : parentMap.get(""), parentMap);
    }

    private List<CategoryDTO> buildTree(List<KnowledgeCategory> roots,
                                         Map<String, List<KnowledgeCategory>> parentMap) {
        List<CategoryDTO> tree = new ArrayList<>();
        for (KnowledgeCategory root : roots) {
            CategoryDTO dto = toDTO(root);
            List<KnowledgeCategory> children = parentMap.get(root.getId());
            if (children != null && !children.isEmpty()) {
                dto.setChildren(buildTree(children, parentMap));
            } else {
                dto.setChildren(new ArrayList<>());
            }
            tree.add(dto);
        }
        return tree;
    }

    public KnowledgeCategory getById(String id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found: " + id));
    }

    public CategoryDTO getDetail(String id) {
        return toDTO(getById(id));
    }

    @Transactional
    public KnowledgeCategory create(KnowledgeCategory category) {
        if (category.getParentId() != null && !category.getParentId().isEmpty()) {
            KnowledgeCategory parent = getById(category.getParentId());
            category.setLevel(parent.getLevel() + 1);
            category.setPath((parent.getPath() != null ? parent.getPath() : "") + "/" + category.getName());
        } else {
            category.setLevel(0);
            category.setPath("/" + category.getName());
        }
        return categoryRepository.save(category);
    }

    @Transactional
    public KnowledgeCategory update(String id, KnowledgeCategory updated) {
        KnowledgeCategory existing = getById(id);
        existing.setName(updated.getName());
        if (updated.getSortOrder() != null) {
            existing.setSortOrder(updated.getSortOrder());
        }
        if (updated.getIcon() != null) {
            existing.setIcon(updated.getIcon());
        }
        if (updated.getDescription() != null) {
            existing.setDescription(updated.getDescription());
        }
        return categoryRepository.save(existing);
    }

    @Transactional
    public void delete(String id) {
        KnowledgeCategory category = getById(id);
        int childCount = categoryRepository.countByParentId(id);
        if (childCount > 0) {
            throw new RuntimeException("Cannot delete category with subcategories");
        }
        int tableCount = tableRepository.countByCategoryId(id);
        if (tableCount > 0) {
            throw new RuntimeException("Cannot delete category with associated tables (count: " + tableCount + ")");
        }
        categoryRepository.deleteById(id);
    }

    @Transactional
    public void updateSort(String id, Integer sortOrder) {
        KnowledgeCategory category = getById(id);
        category.setSortOrder(sortOrder);
        categoryRepository.save(category);
    }

    private CategoryDTO toDTO(KnowledgeCategory entity) {
        CategoryDTO dto = new CategoryDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setParentId(entity.getParentId());
        dto.setLevel(entity.getLevel());
        dto.setPath(entity.getPath());
        dto.setSortOrder(entity.getSortOrder());
        dto.setIcon(entity.getIcon());
        dto.setDescription(entity.getDescription());
        dto.setCreatedAt(entity.getCreatedAt());
        return dto;
    }
}
