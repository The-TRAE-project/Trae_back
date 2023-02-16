package ru.trae.backend.dto.project;

public record NewProjectDto(
        String name,
        String description,
        int period,
        long managerId
) {
}
