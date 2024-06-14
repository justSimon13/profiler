package dev.simonfischer.profiler.services.project;

import dev.simonfischer.profiler.models.dto.KnowledgeDto;
import dev.simonfischer.profiler.models.dto.ProjectDto;
import dev.simonfischer.profiler.models.dto.ProjectListDto;
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


    public void saveProject(ProjectDto projectDto) {
        Project project = modelMapper.map(projectDto, Project.class);
        List<ProjectKnowledge> projectKnowledgeList = createProjectKnowledgeList(projectDto.getKnowledgeList(), project);

        try {
            projectRepository.save(project);
            projectKnowledgeRepository.saveAll(projectKnowledgeList);
        } catch (RuntimeException e) {
            System.err.println(e.getMessage());
            throw new InternalServerException("Failed to save project knowledge for Project");
        }
    }

    public void updateProject(ProjectDto projectDto) {
        Optional<Project> project = projectRepository.findById(projectDto.getId());

        if (project.isEmpty()) {
            throw new ItemNotFoundException("Project not found");
        }

        project.get().setName(projectDto.getName());
        project.get().setCustomer(projectDto.getCustomer());
        project.get().setDescription(projectDto.getDescription());
        project.get().setStart(projectDto.getStart());
        project.get().setEnd(projectDto.getEnd());

        List<Knowledge> knowledgeList =
                projectDto.getKnowledgeList().stream().map(knowledgeDto -> modelMapper.map(knowledgeDto, Knowledge.class)).toList();
        List<Knowledge> knowledgeListDb =
                project.get().getProjectKnowledges().stream().map(ProjectKnowledge::getKnowledge).toList();

        List<Long> idToDelete = GeneralUtility.getDifferences(knowledgeList, knowledgeListDb, Knowledge::getId);
        List<Long> idToSave = GeneralUtility.getDifferences(knowledgeListDb, knowledgeList, Knowledge::getId);

        List<KnowledgeDto> knowledgeListNew = projectDto.getKnowledgeList().stream().filter(knowledge -> idToSave.contains(knowledge.getId())).toList();
        List<ProjectKnowledge> projectKnowledgeList = createProjectKnowledgeList(knowledgeListNew, project.get());

        try {
            deleteProjectKnowledgeById(project.get(), idToDelete, project.get().getProjectKnowledges());
            projectRepository.save(project.get());
            projectKnowledgeRepository.saveAll(projectKnowledgeList);
        } catch (RuntimeException e) {
            throw new InternalServerException("Failed to update project knowledge for Project");
        }
    }

    public ProjectDto getProjectById(Long projectId) {
        Optional<Project> project = projectRepository.findById(projectId);

        if (project.isEmpty()) {
            throw new ItemNotFoundException("Project not found");
        }

        ProjectDto projectDto = modelMapper.map(project, ProjectDto.class);
        List<KnowledgeDto> knowledgeDtoList = project.get().getProjectKnowledges().stream()
                .map(projectKnowledge -> modelMapper.map(projectKnowledge.getKnowledge(), KnowledgeDto.class))
                .toList();
        projectDto.setKnowledgeList(knowledgeDtoList);

        return projectDto;
    }

    public List<ProjectListDto> getProjectList() {
        List<Project> projectListDtos = (List<Project>)projectRepository.findAll();

        return projectListDtos.stream()
                .map(project -> modelMapper.map(project, ProjectListDto.class))
                .toList();
    }

    public List<ProjectDto> getAllProjectDto() {
        List<Long> projectIds = projectRepository.findAllIds();

        List<ProjectDto> projectDtoList = new ArrayList<>();
        for (Long projectId : projectIds) {
            ProjectDto projectDto = getProjectById(projectId);
            projectDtoList.add(projectDto);
        }
        return projectDtoList;
    }

    private List<ProjectKnowledge> createProjectKnowledgeList(List<KnowledgeDto> knowledgeList, Project project) {
        List<ProjectKnowledge> projectKnowledgeList = new ArrayList<>();
        for (KnowledgeDto knowledgeDto : knowledgeList) {
            ProjectKnowledge projectKnowledge = new ProjectKnowledge();
            projectKnowledge.setProject(project);

            if (knowledgeDto.getId() != null) {
                Optional<Knowledge> knowledgeOptional = knowledgeRepository.findById(knowledgeDto.getId());
                knowledgeOptional.ifPresent(projectKnowledge::setKnowledge);
            } else {
                Knowledge savedKnowledge = knowledgeRepository.save(modelMapper.map(knowledgeDto, Knowledge.class));
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
