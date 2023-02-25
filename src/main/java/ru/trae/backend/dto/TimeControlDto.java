package ru.trae.backend.dto;

import java.time.LocalDateTime;
import ru.trae.backend.dto.employee.ShortEmployeeDto;

/**
 * A data transfer object (DTO) used to represent the properties of a time control.
 *
 * @author Vladimir Olennikov
 */
public record TimeControlDto(
        boolean isOnShift,
        boolean autoClosingShift,
        LocalDateTime arrival,
        LocalDateTime departure,
        ShortEmployeeDto employee
) {
}
