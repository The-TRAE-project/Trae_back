package ru.trae.backend.dto.employee;

import java.time.LocalDateTime;
import java.util.List;

/**
 * EmployeeDto is a data transfer object (DTO) used to encapsulate data related to an employee.
 *
 * @author Vladimir Olennikov
 */
public record EmployeeDto(
        long id,
        String firstName,
        String middleName,
        String lastName,
        String phone,
        Integer pinCode,
        boolean isActive,
        LocalDateTime dateOfRegister,
        List<String> types
) {
}
