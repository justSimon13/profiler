package dev.simonfischer.profiler.repositories;

import dev.simonfischer.profiler.models.entity.Knowledge;
import org.springframework.data.repository.CrudRepository;

public interface KnowledgeRepository extends CrudRepository<Knowledge, Long> {
}
