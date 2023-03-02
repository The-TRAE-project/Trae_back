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
        Long phone,
        String username,
        String password
) {
}
