package dev.simonfischer.profiler.repositories;

import dev.simonfischer.profiler.models.entity.KnowledgeCategory;
import org.springframework.data.repository.CrudRepository;

public interface KnowledgeCategoryRepository extends CrudRepository<KnowledgeCategory, Long> {
    boolean existsByKnowledgeList_Id(Long id);
}
