package ru.trae.backend.dto.operation;

public record OperationForEmpDto(
        long operationId,
        String operationName,
        boolean readyToAcceptance,
        boolean isEnded,
        boolean inWork,
        String employeeFirstName,
        String employeeLastName
) {
}
