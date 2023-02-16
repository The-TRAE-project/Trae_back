package ru.trae.backend.dto;

public record NewOperationDto(
        String name,
        String description,
        int period,
        int priority
) {
}
