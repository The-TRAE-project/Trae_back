package ru.trae.backend.dto;

import ru.trae.backend.util.DayOrNight;

import java.time.LocalDateTime;
import java.util.List;

public record WorkShiftingDto(
        LocalDateTime startShift,
        LocalDateTime endShift,
        boolean isEnded,
        DayOrNight timeOfDay,
        List<EmployeeDto> employees
) {
}
