package ru.trae.backend.dto;

public record ManagerRegisterDto(
        String firstName,
        String middleName,
        String lastName,
        Long phone,
        String username,
        String email,
        String password
) {
}
