package ru.trae.backend.dto.employee;

/**
 * This is a data transfer object representing the employee register information.
 *
 * @author Vladimir Olennikov
 */
public record EmployeeRegisterDtoResp(
    String firstName,
    String lastName,
    int pinCode
) {
}
