package dev.simonfischer.profiler.repositories;

import dev.simonfischer.profiler.models.entity.Project;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ProjectRepository extends CrudRepository<Project, Long> {
    @Query("select p.id from Project p")
    List<Long> findAllIds();
}
