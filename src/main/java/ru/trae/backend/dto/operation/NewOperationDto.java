package ru.trae.backend.dto.operation;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * This class represents a DTO for a new operation.
 *
 * @author Vladimir Olennikov
 */
public record NewOperationDto(
    //todo to complete this field
    String name,
    @NotNull(message = "Invalid type work id: id is NULL")
    @Min(value = 0, message = "The type work id cannot be less than 0")
    @Max(value = Integer.MAX_VALUE, message =
        "The type work id cannot be more than " + Integer.MAX_VALUE)
    long typeWorkId
) {
}
