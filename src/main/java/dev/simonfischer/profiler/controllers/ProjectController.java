package dev.simonfischer.profiler.controllers;

import dev.simonfischer.profiler.models.dto.ProjectDto;
import dev.simonfischer.profiler.models.dto.ProjectListDto;
import dev.simonfischer.profiler.services.project.ProjectServiceImpl;
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

    @RequestMapping(value = "", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<String> createUser(@RequestBody ProjectDto projectDto) {
        projectServiceImpl.saveProject(projectDto);
        return new ResponseEntity<>("Project created successfully", HttpStatus.OK);
    }

    @RequestMapping(value = "", method = RequestMethod.PUT, produces = "application/json")
    public ResponseEntity<String> updateUser(@RequestBody ProjectDto projectDto) {
        projectServiceImpl.updateProject(projectDto);
        return new ResponseEntity<>("Project saved successfully", HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<ProjectDto> getProjectById(@PathVariable Long id) {
        ProjectDto projects = projectServiceImpl.getProjectById(id);
        return new ResponseEntity<>(projects, HttpStatus.OK);
    }

    @RequestMapping(value = "all", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<List<ProjectListDto>> getAllProjects() {
        List<ProjectListDto> projects = projectServiceImpl.getProjectList();
        return new ResponseEntity<>(projects, HttpStatus.OK);
    }

}
