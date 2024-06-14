package dev.simonfischer.profiler.models.dto;


import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class ProjectDto {
    private Long id;
    private String customer;
    private String name;
    private String description;
    private LocalDate start;
    private LocalDate end;
    private List<KnowledgeDto> knowledgeList;
}
