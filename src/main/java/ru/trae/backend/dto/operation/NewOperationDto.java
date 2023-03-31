package ru.trae.backend.dto.operation;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import ru.trae.backend.util.RegExpression;

/**
 * This class represents a DTO for a new operation.
 *
 * @author Vladimir Olennikov
 */
public record NewOperationDto(
    @Schema(description = "Название этапа")
    @Pattern(regexp = RegExpression.OPERATION_NAME, message = "Invalid name format")
    String name,
    @NotNull(message = "Invalid type work id: id is NULL")
    @Min(value = 0, message = "The type work id cannot be less than 0")
    @Max(value = Integer.MAX_VALUE, message =
        "The type work id cannot be more than " + Integer.MAX_VALUE)
    long typeWorkId
) {
}
