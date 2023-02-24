package ru.trae.backend.dto.project;

import java.time.LocalDateTime;

public record ProjectAvailableForEmpDto(
        long projectId,
        String customerLastName,
        String projectName,
        String availableOperationName,
        LocalDateTime plannedEndDate
) {
}
