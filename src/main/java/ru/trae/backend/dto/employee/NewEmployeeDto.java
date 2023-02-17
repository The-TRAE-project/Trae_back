package ru.trae.backend.dto.employee;

import java.util.List;

public record NewEmployeeDto(
        String firstName,
        String middleName,
        String lastName,
        Long phone,
        List<Long> typesId
) {
}
