package ru.trae.backend.dto.mapper;

import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.trae.backend.dto.operation.OperationForReportDto;
import ru.trae.backend.entity.task.Operation;

/**
 * The OperationDtoMapper is a Function class that maps an {@link Operation} object to an
 * {@link ru.trae.backend.dto.operation.OperationForReportDto} object.
 *
 * @author Vladimir Olennikov
 */
@Service
@RequiredArgsConstructor
public class OperationForReportDtoMapper implements Function<Operation, OperationForReportDto> {
  
  @Override
  public OperationForReportDto apply(Operation o) {
    
    return new OperationForReportDto(
        o.getId(),
        o.getPriority(),
        o.getName(),
        o.getStartDate(),
        o.getAcceptanceDate(),
        o.getPlannedEndDate(),
        o.getRealEndDate(),
        o.isEnded(),
        o.isInWork(),
        o.isReadyToAcceptance());
  }
}
