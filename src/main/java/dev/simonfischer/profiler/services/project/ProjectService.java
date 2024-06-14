package dev.simonfischer.profiler.services.project;

import dev.simonfischer.profiler.models.dto.ProjectDto;
import dev.simonfischer.profiler.models.dto.ProjectListDto;

import java.util.List;

public interface ProjectService {
    void saveProject(ProjectDto projectDto);
    void updateProject(ProjectDto projectDto);
    ProjectDto getProjectById(Long projectId);
    List<ProjectListDto> getProjectList();
    List<ProjectDto> getAllProjectDto();
}