package ru.trae.backend.dto.mapper;

import org.springframework.stereotype.Service;
import ru.trae.backend.dto.operation.OperationDto;
import ru.trae.backend.dto.employee.ShortEmployeeDto;
import ru.trae.backend.dto.project.ShortProjectDto;
import ru.trae.backend.dto.type.TypeWorkDto;
import ru.trae.backend.entity.task.Operation;
import ru.trae.backend.entity.task.Project;
import ru.trae.backend.entity.user.Employee;

import java.util.function.Function;

@Service
public class OperationDtoMapper implements Function<Operation, OperationDto> {

    @Override
    public OperationDto apply(Operation o) {
        Project p = o.getProject();
        Employee e = o.getEmployee();

        return new OperationDto(
                o.getId(),
                o.getPriority(),
                o.getName(),
                o.getDescription(),
                o.getStartDate(),
                o.getAcceptanceDate(),
                o.getPlannedEndDate(),
                o.getRealEndDate(),
                o.getPeriod(),
                o.isEnded(),
                o.isInWork(),
                o.isReadyToAcceptance(),
                new TypeWorkDto(o.getTypeWork().getId(), o.getTypeWork().getName()),
                new ShortProjectDto(p.getId(), p.getName(), p.getDescription()),
                e == null ? null : new ShortEmployeeDto(e.getId(), e.getFirstName(), e.getLastName())
        );
    }
}
