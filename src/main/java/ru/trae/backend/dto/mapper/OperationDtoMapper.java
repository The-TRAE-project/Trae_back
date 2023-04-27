package ru.trae.backend.dto.mapper;

import static java.time.temporal.ChronoUnit.HOURS;

import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.trae.backend.dto.employee.EmployeeFirstLastNameDto;
import ru.trae.backend.dto.operation.OperationDto;
import ru.trae.backend.entity.task.Operation;
import ru.trae.backend.entity.task.Project;
import ru.trae.backend.entity.user.Employee;

/**
 * The OperationDtoMapper is a Function class that maps an {@link Operation} object to an
 * {@link OperationDto} object.
 *
 * @author Vladimir Olennikov
 */
@Service
@RequiredArgsConstructor
public class OperationDtoMapper implements Function<Operation, OperationDto> {

  @Override
  public OperationDto apply(Operation o) {
    Project p = o.getProject();
    Employee e = o.getEmployee();

    Integer actualPeriod;
    if (o.isEnded()) {
      actualPeriod = Math.toIntExact(HOURS.between(o.getStartDate(), o.getRealEndDate()));
    } else {
      actualPeriod = null;
    }

    return new OperationDto(
        o.getId(),
        o.getPriority(),
        o.getName(),
        o.getStartDate(),
        o.getAcceptanceDate(),
        o.getPlannedEndDate(),
        o.getRealEndDate(),
        o.getPeriod(),
        actualPeriod,
        o.isEnded(),
        o.isInWork(),
        o.isReadyToAcceptance(),
        p.getNumber(),
        o.getTypeWork().getName(),
        e == null ? null : new EmployeeFirstLastNameDto(
            e.getFirstName(),
            e.getLastName()
        ));
  }
}
