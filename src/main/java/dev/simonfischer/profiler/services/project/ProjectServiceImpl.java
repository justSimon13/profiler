package dev.simonfischer.profiler.services.project;

import dev.simonfischer.profiler.models.entity.Knowledge;
import dev.simonfischer.profiler.models.entity.Project;
import dev.simonfischer.profiler.models.entity.ProjectKnowledge;
import dev.simonfischer.profiler.models.exception.entity.InternalServerException;
import dev.simonfischer.profiler.models.exception.entity.ItemNotFoundException;
import dev.simonfischer.profiler.repositories.KnowledgeRepository;
import dev.simonfischer.profiler.repositories.ProjectKnowledgeRepository;
import dev.simonfischer.profiler.repositories.ProjectRepository;
import dev.simonfischer.profiler.utility.GeneralUtility;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProjectServiceImpl implements ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private KnowledgeRepository knowledgeRepository;

    @Autowired
    private ProjectKnowledgeRepository projectKnowledgeRepository;

    @Autowired
    private ModelMapper modelMapper;


    public void saveProject(Project project, List<Knowledge> knowledgeList) {
        try {
            List<ProjectKnowledge> projectKnowledgeList = createProjectKnowledgeList(knowledgeList, project);
            projectRepository.save(project);
            projectKnowledgeRepository.saveAll(projectKnowledgeList);
        } catch (RuntimeException e) {
            System.err.println(e.getMessage());
            throw new InternalServerException("Failed to save project knowledge for Project");
        }
    }

    public void updateProject(Project project, List<Knowledge> knowledgeList) {
        Optional<Project> projectDb = projectRepository.findById(project.getId());

        if (projectDb.isEmpty()) {
            throw new ItemNotFoundException("Project not found");
        }

        projectDb.get().setName(project.getName());
        projectDb.get().setCustomer(project.getCustomer());
        projectDb.get().setDescription(project.getDescription());
        projectDb.get().setStart(project.getStart());
        projectDb.get().setEnd(project.getEnd());

        List<Knowledge> knowledgeListDb =
                projectDb.get().getProjectKnowledges().stream().map(ProjectKnowledge::getKnowledge).toList();

        List<Long> idToDelete = GeneralUtility.getDifferences(knowledgeList, knowledgeListDb, Knowledge::getId);
        List<Long> idToSave = GeneralUtility.getDifferences(knowledgeListDb, knowledgeList, Knowledge::getId);

        List<Knowledge> knowledgeListNew = knowledgeList.stream().filter(knowledge -> idToSave.contains(knowledge.getId())).toList();
        List<ProjectKnowledge> projectKnowledgeList = createProjectKnowledgeList(knowledgeListNew, projectDb.get());

        try {
            deleteProjectKnowledgeById(projectDb.get(), idToDelete, projectDb.get().getProjectKnowledges());
            projectRepository.save(projectDb.get());
            projectKnowledgeRepository.saveAll(projectKnowledgeList);
        } catch (RuntimeException e) {
            throw new InternalServerException("Failed to update project knowledge for Project");
        }
    }

    public Project getProjectById(Long projectId) {
        Optional<Project> project = projectRepository.findById(projectId);

        if (project.isEmpty()) {
            throw new ItemNotFoundException("Project not found");
        }

        return project.get();
    }

    public List<Project> getProjectList() {
        return  (List<Project>)projectRepository.findAll();
    }

    private List<ProjectKnowledge> createProjectKnowledgeList(List<Knowledge> knowledgeList, Project project) {
        List<ProjectKnowledge> projectKnowledgeList = new ArrayList<>();
        for (Knowledge knowledge : knowledgeList) {
            ProjectKnowledge projectKnowledge = new ProjectKnowledge();
            projectKnowledge.setProject(project);

            if (knowledge.getId() != null) {
                Optional<Knowledge> knowledgeOptional = knowledgeRepository.findById(knowledge.getId());
                knowledgeOptional.ifPresent(projectKnowledge::setKnowledge);
            } else {
                Knowledge savedKnowledge = knowledgeRepository.save(knowledge);
                projectKnowledge.setKnowledge(savedKnowledge);
            }

            projectKnowledgeList.add(projectKnowledge);
        }
        return projectKnowledgeList;
    }

    private void deleteProjectKnowledgeById(Project project, List<Long> idToDelete, List<ProjectKnowledge> projectKnowledgeList) {
        if (!idToDelete.isEmpty()) {
            List<ProjectKnowledge> projectKnowledgeToDelete = new ArrayList<>();

            for (ProjectKnowledge projectKnowledge : projectKnowledgeList) {
                if (idToDelete.contains(projectKnowledge.getKnowledge().getId())) {
                    projectKnowledgeToDelete.add(projectKnowledge);
                }
            }

            project.getProjectKnowledges().removeAll(projectKnowledgeToDelete);

            for (ProjectKnowledge projectKnowledge : projectKnowledgeToDelete) {
                projectKnowledge.setProject(null);
                projectKnowledgeRepository.delete(projectKnowledge);
            }
        }
    }
}
