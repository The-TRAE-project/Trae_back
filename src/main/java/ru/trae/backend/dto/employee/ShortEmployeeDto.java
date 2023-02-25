package ru.trae.backend.dto.employee;

/**
 * Short Employee Data Transfer Object (DTO) containing basic employee information.
 *
 * @author Vladimir Olennikov
 */
public record ShortEmployeeDto(
        Long id,
        String firstName,
        String lastName
) {
}
