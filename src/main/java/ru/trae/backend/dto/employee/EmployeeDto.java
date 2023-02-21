package ru.trae.backend.dto.employee;

import java.util.List;

public record EmployeeDto(
        Long id,
        String firstName,
        String middleName,
        String lastName,
        Long phone,
        Integer pinCode,
        boolean isActive,
        List<String> types
) {
}
