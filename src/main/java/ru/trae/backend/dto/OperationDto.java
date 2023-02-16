package ru.trae.backend.dto;

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
        ShortProjectDto shortProjectDto,
        ShortEmployeeDto shortEmployeeDto
) {
}
