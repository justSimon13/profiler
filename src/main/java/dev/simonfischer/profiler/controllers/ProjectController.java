package dev.simonfischer.profiler.controllers;

import dev.simonfischer.profiler.models.dto.KnowledgeDto;
import dev.simonfischer.profiler.models.dto.ProjectDto;
import dev.simonfischer.profiler.models.dto.ProjectListDto;
import dev.simonfischer.profiler.models.entity.Knowledge;
import dev.simonfischer.profiler.models.entity.Project;
import dev.simonfischer.profiler.services.project.ProjectServiceImpl;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/project")
public class ProjectController {

    @Autowired
    private ProjectServiceImpl projectServiceImpl;

    @Autowired
    private ModelMapper modelMapper;

    @RequestMapping(value = "", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<String> createProject(@RequestBody ProjectDto projectDto) {
        Project project = modelMapper.map(projectDto, Project.class);
        List<Knowledge> knowledgeList = projectDto.getKnowledgeList()
                .stream()
                .map(knowledgeDto -> modelMapper.map(knowledgeDto, Knowledge.class))
                .toList();
        projectServiceImpl.saveProject(project, knowledgeList);
        return new ResponseEntity<>("Project created successfully", HttpStatus.OK);
    }

    @RequestMapping(value = "", method = RequestMethod.PUT, produces = "application/json")
    public ResponseEntity<String> updateProject(@RequestBody ProjectDto projectDto) {
        Project project = modelMapper.map(projectDto, Project.class);
        List<Knowledge> knowledgeList = projectDto.getKnowledgeList()
                .stream()
                .map(knowledgeDto -> modelMapper.map(knowledgeDto, Knowledge.class))
                .toList();
        projectServiceImpl.updateProject(project, knowledgeList);
        return new ResponseEntity<>("Project saved successfully", HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<ProjectDto> getProjectById(@PathVariable Long id) {
        Project project = projectServiceImpl.getProjectById(id);

        ProjectDto projectDto = modelMapper.map(project, ProjectDto.class);
        List<KnowledgeDto> knowledgeDtoList = project.getProjectKnowledges()
                .stream()
                .map(projectKnowledge -> modelMapper.map(projectKnowledge.getKnowledge(), KnowledgeDto.class))
                .toList();
        projectDto.setKnowledgeList(knowledgeDtoList);

        return new ResponseEntity<>(projectDto, HttpStatus.OK);
    }

    @RequestMapping(value = "all", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<List<ProjectListDto>> getAllProjects() {
        List<ProjectListDto> projects = projectServiceImpl.getProjectList()
                .stream()
                .map(project -> modelMapper.map(project, ProjectListDto.class))
                .toList();
        return new ResponseEntity<>(projects, HttpStatus.OK);
    }

}
