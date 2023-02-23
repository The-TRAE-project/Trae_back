package ru.trae.backend.dto.operation;

import ru.trae.backend.dto.employee.ShortEmployeeDto;
import ru.trae.backend.dto.project.ShortProjectDto;
import ru.trae.backend.dto.type.TypeWorkDto;

import java.time.LocalDateTime;

public record OperationDto(
        long id,
        int priority,
        String name,
        String description,
        LocalDateTime startDate,
        LocalDateTime acceptanceDate,
        LocalDateTime plannedEndDate,
        LocalDateTime realEndDate,
        int period,
        boolean isEnded,
        boolean inWork,
        boolean readyToAcceptance,
        TypeWorkDto typeWorkDto,
        ShortProjectDto shortProjectDto,
        ShortEmployeeDto shortEmployeeDto
) {
}
