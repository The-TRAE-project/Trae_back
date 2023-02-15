package ru.trae.backend.dto;

public record NewProjectDto(
        String name,
        String description,
        int period,
        long managerId
) {
}
