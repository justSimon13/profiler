package dev.simonfischer.profiler.services.knowledge;

import dev.simonfischer.profiler.models.dto.KnowledgeCategoryDto;

import java.util.List;

public interface KnowledgeService {

    void updateKnowledgeCategoryList(List<KnowledgeCategoryDto> knowledgeCategoryDtos);

    List<KnowledgeCategoryDto> getKnowledgeCategoryList();
}
