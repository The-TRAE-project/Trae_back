package ru.trae.backend.dto.project;

public record ProjectAvailableForEmpDto(
        long projectId,
        String customerLastName,
        String projectName,
        String availableOperationName
) {
}
