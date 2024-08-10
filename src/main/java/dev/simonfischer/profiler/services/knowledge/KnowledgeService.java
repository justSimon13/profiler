package dev.simonfischer.profiler.services.knowledge;

import dev.simonfischer.profiler.models.entity.KnowledgeCategory;

import java.util.List;

public interface KnowledgeService {

    void updateKnowledgeCategoryList(List<KnowledgeCategory> knowledgeCategories);

    List<KnowledgeCategory> getKnowledgeCategoryList();
}
