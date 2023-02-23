package ru.trae.backend.dto.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.trae.backend.dto.WorkingShiftDto;
import ru.trae.backend.entity.WorkingShift;

import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class WorkingShiftDtoMapper implements Function<WorkingShift, WorkingShiftDto> {

    private final TimeControlMapper timeControlMapper;

    @Override
    public WorkingShiftDto apply(WorkingShift ws) {
        return new WorkingShiftDto(
                ws.getStartShift(),
                ws.getEndShift(),
                ws.isEnded(),
                ws.getTimeControls().stream()
                        .map(timeControlMapper)
                        .toList()
        );
    }
}
