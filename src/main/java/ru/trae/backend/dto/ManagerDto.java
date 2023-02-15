package ru.trae.backend.dto;

public record ManagerDto(
        long id,
        String firstName,
        String middleName,
        String lastName,
        Long phone,
        String email
) {
}
