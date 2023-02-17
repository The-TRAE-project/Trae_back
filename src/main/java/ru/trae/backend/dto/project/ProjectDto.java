package ru.trae.backend.dto.project;

import ru.trae.backend.dto.manager.ManagerDto;
import ru.trae.backend.dto.operation.OperationDto;

import java.time.LocalDateTime;
import java.util.List;

public record ProjectDto(
        long id,
        String name,
        String description,
        LocalDateTime startDate,
        LocalDateTime plannedEndDate,
        LocalDateTime realEndDate,
        int period,
        boolean isEnded,
        List<OperationDto> operations,
        ManagerDto managerDto
) {

}
