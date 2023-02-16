package ru.trae.backend.dto.operation;

public record NewOperationDto(
        String name,
        String description,
        int period,
        int priority
) {
}
