package ru.trae.backend.dto.manager;

/**
 * ManagerDto is a data transfer object (DTO) class used to encapsulate data related to a manager.
 *
 * @author Vladimir Olennikov
 */
public record ManagerDto(
    long id,
    String firstName,
    String middleName,
    String lastName,
    String phone,
    String role,
    String dateOfRegister,
    String dateOfEmployment
) {
}
