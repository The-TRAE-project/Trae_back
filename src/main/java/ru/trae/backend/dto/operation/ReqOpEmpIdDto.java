package ru.trae.backend.dto.operation;

/**
 * The {@code ReqOpEmpIdDto} class represents a data transfer object for requesting
 * operation and employee ID.
 *
 * @author Vladimir Olennikov
 */
public record ReqOpEmpIdDto(
        long operationId,
        long employeeId
) {
}
