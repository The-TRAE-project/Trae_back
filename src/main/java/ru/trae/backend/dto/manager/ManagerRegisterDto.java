package ru.trae.backend.dto.manager;

/**
 * The ManagerRegisterDto class is a data transfer object used to register a new manager.
 *
 * @author Vladimir Olennikov
 */
public record ManagerRegisterDto(
        String firstName,
        String middleName,
        String lastName,
        String phone,
        String username
) {
}
