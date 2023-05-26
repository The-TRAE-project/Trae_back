package ru.trae.backend.dto.operation;

import java.time.LocalDateTime;

/**
 * The OperationForReportDto class is used to represent an operation.
 *
 * @author Vladimir Olennikov
 */
public record OperationForReportDto(
    long id,
    int priority,
    String name,
    LocalDateTime startDate,
    LocalDateTime acceptanceDate,
    LocalDateTime plannedEndDate,
    LocalDateTime realEndDate,
    boolean isEnded,
    boolean inWork,
    boolean readyToAcceptance
) {
}
