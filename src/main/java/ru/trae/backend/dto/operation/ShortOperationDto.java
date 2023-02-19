package ru.trae.backend.dto.operation;

public record ShortOperationDto(
        long id,
        long projectId,
        int priority,
        String name,
        String description,
        String nameTypeWork,
        long typeWork,
        boolean isEnded,
        boolean inWork,
        boolean readyToAcceptance
) {
}
