package dev.simonfischer.profiler.services.project;

import dev.simonfischer.profiler.models.entity.Knowledge;
import dev.simonfischer.profiler.models.entity.Project;

import java.util.List;

public interface ProjectService {
    void saveProject(Project project, List<Knowledge> knowledgeList);
    void updateProject(Project project, List<Knowledge> knowledgeList);
    Project getProjectById(Long projectId);
    List<Project> getProjectList();
}