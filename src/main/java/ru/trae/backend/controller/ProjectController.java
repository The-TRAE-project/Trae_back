package ru.trae.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.trae.backend.dto.project.NewProjectDto;
import ru.trae.backend.dto.project.ProjectDto;
import ru.trae.backend.service.ProjectService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/project")
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping("/new")
    public ResponseEntity<ProjectDto> projectPersist(@RequestBody NewProjectDto dto) {
        ProjectDto pDto = projectService.convertFromProject(projectService.saveNewProject(dto));
        return ResponseEntity.ok(pDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectDto> project(@PathVariable long id) {
        return ResponseEntity.ok(projectService.getProjectDtoById(id));
    }

    @GetMapping("/projects")
    public ResponseEntity<List<ProjectDto>> project() {
        return ResponseEntity.ok(projectService.getAllProjects());
    }

}
