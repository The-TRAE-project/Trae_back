package ru.trae.backend.dto.employee;

import java.time.LocalDate;
import java.util.List;
import ru.trae.backend.dto.type.TypeWorkDto;

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
    LocalDate dateOfRegister,
    LocalDate dateOfEmployment,
    LocalDate dateOfDismissal,
    List<TypeWorkDto> types
) {
}
