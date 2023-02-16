package ru.trae.backend.dto.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.trae.backend.dto.project.ProjectDto;
import ru.trae.backend.entity.task.Project;

import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class ProjectDtoMapper implements Function<Project, ProjectDto> {
    private final ManagerDtoMapper managerDtoMapper;
    private final ShortOperationDtoMapper shortOperationDtoMapper;

    @Override
    public ProjectDto apply(Project p) {
        return new ProjectDto(
                p.getId(),
                p.getName(),
                p.getDescription(),
                p.getStartDate(),
                p.getEndDate(),
                p.getPeriod(),
                p.isEnded(),
                p.getOperations().stream()
                        .map(shortOperationDtoMapper)
                        .toList(),
                managerDtoMapper.apply(p.getManager())
        );
    }
}
