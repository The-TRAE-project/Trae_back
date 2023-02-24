package ru.trae.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.trae.backend.dto.project.NewProjectDto;
import ru.trae.backend.dto.project.ProjectAvailableForEmpDto;
import ru.trae.backend.dto.project.ProjectDto;
import ru.trae.backend.service.ProjectService;

import java.util.List;
import java.util.Map;

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
    public ResponseEntity<List<ProjectDto>> projects() {
        return ResponseEntity.ok(projectService.getAllProjects());
    }

    @GetMapping("/employee/available-projects/{employeeId}")
    public ResponseEntity<List<ProjectAvailableForEmpDto>> availableProjectsByEmpId(@PathVariable long employeeId) {
        return ResponseEntity.ok(projectService.getAvailableProjects(employeeId));
    }

    @GetMapping("/employee/grouping-available-projects/{employeeId}")
    public ResponseEntity<Map<String, List<Long>>> groupingAvailableProjectsByEmpId(@PathVariable long employeeId) {
        return ResponseEntity.ok(projectService.getGroupingAvailableProjectsId(employeeId));
    }

    @PostMapping("/employee/grouped-available-projects/")
    public ResponseEntity<List<ProjectAvailableForEmpDto>> groupingAvailableProjectsByEmpId(@RequestBody List<Long> projectIds) {
        return ResponseEntity.ok(projectService.getGroupedAvailableProjects(projectIds));
    }

}
