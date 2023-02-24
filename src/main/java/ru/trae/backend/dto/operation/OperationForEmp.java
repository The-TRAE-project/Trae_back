package ru.trae.backend.dto.operation;

public record OperationForEmp(
        long operationId,
        String name,
        boolean readyToAcceptance,
        boolean isEnded,
        boolean inWork,
        String employeeFirstName,
        String employeeLastName
) {
}
