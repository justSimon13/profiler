package dev.simonfischer.profiler.models.business;

import dev.simonfischer.profiler.models.entity.KnowledgeCategory;
import dev.simonfischer.profiler.models.entity.Project;
import dev.simonfischer.profiler.models.entity.User;
import lombok.Data;

import java.util.List;

@Data
public class Profile {
    private User user;
    private List<KnowledgeCategory> knowledgeCategoryList;
    private List<Project> projectList;
}
