package ru.trae.backend.dto.operation;

import java.time.LocalDateTime;
import ru.trae.backend.dto.employee.EmployeeFirstLastNameDto;
import ru.trae.backend.dto.employee.ShortEmployeeDto;
import ru.trae.backend.dto.type.TypeWorkDto;

/**
 * The OperationDto class is used to represent an operation.
 *
 * @author Vladimir Olennikov
 */
public record OperationDto(
    long id,
    int priority,
    String name,
    LocalDateTime startDate,
    LocalDateTime acceptanceDate,
    LocalDateTime plannedEndDate,
    LocalDateTime realEndDate,
    int period,
    Integer actualPeriod,
    boolean isEnded,
    boolean inWork,
    boolean readyToAcceptance,
    int projectNumber,
    String typeWorkName,
    EmployeeFirstLastNameDto employeeFirstLastNameDto
) {
}
