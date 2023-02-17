package ru.trae.backend.dto.employee;

public record EmployeeDto(
        Long id,
        String firstName,
        String middleName,
        String lastName,
        Long phone,
        Integer pinCode
) {
}
