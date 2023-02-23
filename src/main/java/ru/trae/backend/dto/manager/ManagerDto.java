package ru.trae.backend.dto.manager;

public record ManagerDto(
        long id,
        String firstName,
        String middleName,
        String lastName,
        Long phone,
        String email,
        String role,
        String dateOfRegister
) {
}
