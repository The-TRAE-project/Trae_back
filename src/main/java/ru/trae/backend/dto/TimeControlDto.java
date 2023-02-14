package ru.trae.backend.dto;

import java.time.LocalDateTime;

public record TimeControlDto(
         boolean isOnShift,
         LocalDateTime arrival,
         LocalDateTime departure,
         EmployeeDto employee
) {
}
