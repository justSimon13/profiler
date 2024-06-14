package dev.simonfischer.profiler.models.dto;

import lombok.Data;

import java.util.List;

@Data
public class ProfileDto {
    private UserDto user;
    private List<KnowledgeCategoryDto> knowledgeCategoryList;
    private List<ProjectDto> projectList;
}
