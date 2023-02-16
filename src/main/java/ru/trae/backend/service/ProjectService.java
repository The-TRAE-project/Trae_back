package ru.trae.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.trae.backend.dto.NewProjectDto;
import ru.trae.backend.dto.OrderDto;
import ru.trae.backend.dto.ProjectDto;
import ru.trae.backend.dto.mapper.ProjectDtoMapper;
import ru.trae.backend.entity.task.Order;
import ru.trae.backend.entity.task.Project;
import ru.trae.backend.exceptionhandler.exception.OrderException;
import ru.trae.backend.exceptionhandler.exception.ProjectException;
import ru.trae.backend.repository.ProjectRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final ManagerService managerService;
    private final ProjectDtoMapper projectDtoMapper;

    public Project saveNewProject(NewProjectDto dto) {
        Project p = new Project();
        p.setName(dto.name());
        p.setDescription(dto.description());
        p.setPeriod(dto.period());
        p.setStartDate(LocalDateTime.now());
        p.setEnded(false);
        p.setManager(managerService.getManagerById(dto.managerId()));
        p.setOperations(new ArrayList<>());

        return projectRepository.save(p);
    }

    public Project getProjectById(long id) {
        return projectRepository.findById(id).orElseThrow(
                () -> new ProjectException(HttpStatus.NOT_FOUND, "Проект с ID " + id + " не найден"));
    }

    public List<ProjectDto> getAllProjects() {
        return projectRepository.findAll()
                .stream()
                .map(projectDtoMapper)
                .toList();
    }

    public ProjectDto getProjectDtoById(long id) {
        return projectDtoMapper.apply(getProjectById(id));
    }

    public ProjectDto convertFromProject(Project p) {
        return projectDtoMapper.apply(p);
    }
}
