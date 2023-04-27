package ru.trae.backend.dto.mapper;

import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.trae.backend.dto.operation.OperationDto;
import ru.trae.backend.dto.operation.OperationInfoForProjectTemplateDto;
import ru.trae.backend.entity.task.Operation;

/**
 * The OperationInfoForProjectTemplateDtoMapper is a Function class that maps
 * an {@link Operation} object to an {@link OperationInfoForProjectTemplateDto} object.
 *
 * @author Vladimir Olennikov
 */
@Service
@RequiredArgsConstructor
public class OperationInfoForProjectTemplateDtoMapper implements
    Function<Operation, OperationInfoForProjectTemplateDto> {

  @Override
  public OperationInfoForProjectTemplateDto apply(Operation o) {
    return new OperationInfoForProjectTemplateDto(
        o.getName(),
        o.isEnded(),
        o.isInWork(),
        o.isReadyToAcceptance()
    );
  }
}
