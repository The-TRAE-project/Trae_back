package ru.trae.backend.dto.employee;

public record NewEmployeeDto(
        String firstName,
        String middleName,
        String lastName,
        Long phone
) {
}
