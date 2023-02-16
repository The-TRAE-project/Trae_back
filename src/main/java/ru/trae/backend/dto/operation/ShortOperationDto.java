package ru.trae.backend.dto.operation;

public record ShortOperationDto(
        long id,
        long projectId,
        int priority,
        String name,
        String description,
        int period,
        boolean isEnded,
        boolean inWork
) {
}
