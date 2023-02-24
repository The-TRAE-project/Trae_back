package ru.trae.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.trae.backend.dto.project.NewProjectDto;
import ru.trae.backend.dto.project.ProjectAvailableForEmpDto;
import ru.trae.backend.dto.project.ProjectDto;
import ru.trae.backend.dto.mapper.ProjectDtoMapper;
import ru.trae.backend.entity.task.Operation;
import ru.trae.backend.entity.task.Project;
import ru.trae.backend.entity.user.Employee;
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
    private final OrderService orderService;
    private final EmployeeService employeeService;
    private final ProjectDtoMapper projectDtoMapper;

    public Project saveNewProject(NewProjectDto dto) {
        Project p = new Project();
        p.setName(dto.name());
        p.setDescription(dto.description());
        p.setPeriod(dto.period());
        p.setStartDate(LocalDateTime.now());
        p.setPlannedEndDate(LocalDateTime.now().plusDays(dto.period()));
        p.setRealEndDate(null);
        p.setEnded(false);
        p.setManager(managerService.getManagerById(dto.managerId()));
        p.setOrder(orderService.getOrderById(dto.orderId()));

        return projectRepository.save(p);
    }

    public Project getProjectById(long id) {
        return projectRepository.findById(id).orElseThrow(
                () -> new ProjectException(HttpStatus.NOT_FOUND, "Project with ID: " + id + " not found"));
    }

    public List<ProjectDto> getAllProjects() {
        return projectRepository.findAll()
                .stream()
                .map(projectDtoMapper)
                .toList();
    }

    public List<ProjectAvailableForEmpDto> getAvailableProjects(long employeeId) {
        Employee e = employeeService.getEmployeeById(employeeId);
        List<Project> projects = new ArrayList<>();

        e.getTypeWorks().forEach(tw -> projects.addAll(projectRepository.findAvailableProjectsByTypeWork(tw.getId())));

        return projects.stream().map(p -> new ProjectAvailableForEmpDto(
                p.getId(),
                p.getOrder().getCustomer().getLastName(),
                p.getName(),
                p.getOperations().stream().filter(Operation::isReadyToAcceptance).findFirst().get().getName(),
                p.getPlannedEndDate()
        )).toList();
    }

    public void updatePlannedEndDate(LocalDateTime newPlannedEndDate, long projectId) {
        projectRepository.updatePlannedEndDateById(newPlannedEndDate, projectId);
    }

    public ProjectDto getProjectDtoById(long id) {
        return projectDtoMapper.apply(getProjectById(id));
    }

    public ProjectDto convertFromProject(Project p) {
        return projectDtoMapper.apply(p);
    }
}
