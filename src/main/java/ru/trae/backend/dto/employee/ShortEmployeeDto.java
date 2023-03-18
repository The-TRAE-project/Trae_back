package ru.trae.backend.dto.employee;

/**
 * Short Employee Data Transfer Object (DTO) containing basic employee information.
 *
 * @author Vladimir Olennikov
 */
public record ShortEmployeeDto(
    long id,
    String firstName,
    String lastName,
    boolean onShift
) {
}
