package ru.trae.backend.dto.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.trae.backend.dto.project.ProjectAvailableForEmpDto;
import ru.trae.backend.entity.task.Operation;
import ru.trae.backend.entity.task.Project;

import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class ProjectAvailableDtoMapper implements Function<Project, ProjectAvailableForEmpDto> {

    @Override
    public ProjectAvailableForEmpDto apply(Project p) {
        return new ProjectAvailableForEmpDto(
                p.getId(),
                p.getOrder().getCustomer().getLastName(),
                p.getName(),
                p.getOperations().stream()
                        .filter(Operation::isReadyToAcceptance)
                        .findFirst()
                        .get().getName()
        );
    }
}
