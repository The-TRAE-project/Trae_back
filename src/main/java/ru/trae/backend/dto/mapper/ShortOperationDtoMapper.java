package ru.trae.backend.dto.mapper;

import org.springframework.stereotype.Service;
import ru.trae.backend.dto.operation.ShortOperationDto;
import ru.trae.backend.entity.task.Operation;
import ru.trae.backend.entity.task.Project;

import java.util.function.Function;

@Service
public class ShortOperationDtoMapper implements Function<Operation, ShortOperationDto> {

    @Override
    public ShortOperationDto apply(Operation o) {
        Project p = o.getProject();

        return new ShortOperationDto(
                o.getId(),
                p.getId(),
                o.getPriority(),
                o.getName(),
                o.getDescription(),
                o.getTypeWork().getName(),
                o.getTypeWork().getId(),
                o.isEnded(),
                o.isInWork(),
                o.isReadyToAcceptance()
        );
    }
}
