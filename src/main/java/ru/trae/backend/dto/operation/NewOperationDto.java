package ru.trae.backend.dto.operation;

public record NewOperationDto(
        String name,
        String description,
        long typeWorkId,
        int priority

) {
}
