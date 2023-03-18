package ru.trae.backend.dto.operation;

/**
 * This class represents an operation for an employee.
 *
 * @author Vladimir Olennikov
 */
public record OperationForEmpDto(
    long id,
    String name,
    boolean readyToAcceptance,
    boolean isEnded,
    boolean inWork,
    String employeeFirstName,
    String employeeLastName
) {
}
