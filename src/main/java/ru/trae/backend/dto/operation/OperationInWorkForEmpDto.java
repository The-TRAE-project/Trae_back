package ru.trae.backend.dto.operation;

/**
 * Data Transfer Object (DTO) used to store information about operations in work for an employee.
 *
 * @author Vladimir Olennikov
 */
public record OperationInWorkForEmpDto(
    long operationId,
    long projectId,
    long projectNumber,
    String projectName,
    String operationName,
    String customerLastName
) {
}
