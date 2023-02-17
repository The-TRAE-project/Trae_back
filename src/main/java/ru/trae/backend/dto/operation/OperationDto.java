package ru.trae.backend.dto.operation;

import ru.trae.backend.dto.employee.ShortEmployeeDto;
import ru.trae.backend.dto.project.ShortProjectDto;
import ru.trae.backend.dto.type.TypeWorkDto;
import ru.trae.backend.entity.TypeWork;

import java.time.LocalDateTime;

public record OperationDto(
        long id,
        int priority,
        String name,
        String description,
        LocalDateTime startDate,
        int period,
        boolean isEnded,
        boolean inWork,
        TypeWorkDto typeWorkDto,
        ShortProjectDto shortProjectDto,
        ShortEmployeeDto shortEmployeeDto
) {
}
