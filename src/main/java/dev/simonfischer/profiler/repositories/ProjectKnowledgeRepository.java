package dev.simonfischer.profiler.repositories;

import dev.simonfischer.profiler.models.entity.ProjectKnowledge;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectKnowledgeRepository extends CrudRepository<ProjectKnowledge, Long> {
    List<ProjectKnowledge> findAllByProjectId(Long projectId);
    Optional<ProjectKnowledge> findByProjectIdAndKnowledgeId(Long projectId, Long knowledgeId);
}
