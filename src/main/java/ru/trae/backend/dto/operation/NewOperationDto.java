package ru.trae.backend.dto.operation;

/**
 * This class represents a DTO for a new operation.
 *
 * @author Vladimir Olennikov
 */
public record NewOperationDto(
        String name,
        long typeWorkId,
        int priority
) {
}
