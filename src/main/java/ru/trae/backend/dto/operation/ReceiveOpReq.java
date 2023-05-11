package ru.trae.backend.dto.operation;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * The {@code ReceiveOpReq} class represents a data transfer object for requesting
 * operation and employee ID and operation priority.
 *
 * @author Vladimir Olennikov
 */
public record ReceiveOpReq(
    @NotNull(message = "Invalid operation id: id is NULL")
    @Min(value = 0, message = "The operation id cannot be less than 0")
    @Max(value = Integer.MAX_VALUE, message =
        "The operation id cannot be more than " + Integer.MAX_VALUE)
    Long operationId,
    @NotNull(message = "Invalid operation priority: priority is NULL")
    @Min(value = 0, message = "The operation priority cannot be less than 0")
    @Max(value = 999, message = "The operation priority cannot be more than 999")
    Integer operationPriority,
    @NotNull(message = "Invalid employee id: id is NULL")
    @Min(value = 0, message = "The employee id cannot be less than 0")
    @Max(value = Integer.MAX_VALUE, message =
        "The employee id cannot be more than " + Integer.MAX_VALUE)
    Long employeeId
) {
}
