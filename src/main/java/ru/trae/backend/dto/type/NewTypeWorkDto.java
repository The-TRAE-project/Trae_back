package ru.trae.backend.dto.type;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.Pattern;
import ru.trae.backend.util.RegExpression;

/**
 * The {@code NewTypeWorkDto} class represents a data transfer object (DTO) used to represent
 * a new type of work.
 *
 * @author Vladimir Olennikov
 */
public record NewTypeWorkDto(
    @Schema(description = "Новое название типа работы")
    @Pattern(regexp = RegExpression.TYPE_WORK_NAME, message = "Invalid name format")
    String name
) {
}
