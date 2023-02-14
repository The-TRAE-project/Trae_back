package ru.trae.backend.dto;

public record CustomerDto(
        String firstName,
        String middleName,
        String lastName,
        Integer phone
) {
}
