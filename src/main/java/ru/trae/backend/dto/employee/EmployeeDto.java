package ru.trae.backend.dto.employee;

import java.time.LocalDateTime;
import java.util.List;

public record EmployeeDto(Long id, String firstName, String middleName, String lastName, Long phone, Integer pinCode,
		boolean isActive, LocalDateTime dateOfRegister, List<String> types) {
}
