package ru.trae.backend.dto;

public record EmployeeDto(
        String firstName,
        String middleName,
        String lastName,
        Long phone,
        Integer pinCode
) {
}
