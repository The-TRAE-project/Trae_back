package ru.trae.backend.dto.project;

import java.time.LocalDateTime;
import java.util.List;
import ru.trae.backend.dto.operation.OperationForReportDto;

/**
 * This class represents a Project Data Transfer Object (DTO).
 * It contains the data associated with a project, such as its ID, number, name, description,
 * start date, planned end date, real end date, period, if it is ended or not, a list of operations.
 *
 * @author Vladimir Olennikov
 */
public record ProjectForReportDto(
    long id,
    int number,
    String name,
    LocalDateTime startDate,
    LocalDateTime startFirstOperationDate,
    LocalDateTime plannedEndDate,
    LocalDateTime endDateInContract,
    LocalDateTime realEndDate,
    boolean isEnded,
    int operationPeriod,
    List<OperationForReportDto> operations,
    String customer,
    String comment
) {
}
