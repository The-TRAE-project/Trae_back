package ru.trae.backend.dto;

import ru.trae.backend.dto.employee.ShortEmployeeDto;

import java.time.LocalDateTime;

public record TimeControlDto(
         boolean isOnShift,
         LocalDateTime arrival,
         LocalDateTime departure,
         ShortEmployeeDto employee
) {
}
