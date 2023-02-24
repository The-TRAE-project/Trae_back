package ru.trae.backend.dto.operation;

public record OperationInWorkForEmpDto(
        long operationId,
        long projectId,
        String projectName,
        String operationName,
        String employeeFirstName,
        String employeeLastName
) {
}
