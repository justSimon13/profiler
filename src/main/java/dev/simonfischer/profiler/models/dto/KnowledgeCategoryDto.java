package dev.simonfischer.profiler.models.dto;

import lombok.Data;

import java.util.List;

@Data
public class KnowledgeCategoryDto {
    private Long id;
    private String name;
    private List<KnowledgeDto> knowledgeList;
}
