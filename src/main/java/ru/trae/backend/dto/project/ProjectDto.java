package ru.trae.backend.dto.project;

import java.time.LocalDateTime;
import java.util.List;
import ru.trae.backend.dto.manager.ManagerDto;
import ru.trae.backend.dto.operation.OperationDto;

/**
 * This class represents a Project Data Transfer Object (DTO).
 * It contains the data associated with a project, such as its ID, number, name, description,
 * start date, planned end date, real end date, period, if it is ended or not, a list of operations
 * and a manager DTO.
 *
 * @author Vladimir Olennikov
 */
public record ProjectDto(
    long id,
    long number,
    String name,
    LocalDateTime startDate,
    LocalDateTime plannedEndDate,
    LocalDateTime realEndDate,
    int period,
    Integer actualPeriod,
    boolean isEnded,
    List<OperationDto> operations,
    ManagerDto managerDto,
    String customer,
    String comment
) {
}
