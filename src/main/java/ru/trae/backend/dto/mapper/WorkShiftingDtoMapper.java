package ru.trae.backend.dto.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.trae.backend.dto.WorkShiftingDto;
import ru.trae.backend.entity.WorkShifting;

import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class WorkShiftingDtoMapper implements Function<WorkShifting, WorkShiftingDto> {

    private final EmployeeDtoMapper employeeDtoMapper;

    @Override
    public WorkShiftingDto apply(WorkShifting workShifting) {
        return new WorkShiftingDto(
                workShifting.getStartShift(),
                workShifting.getEndShift(),
                workShifting.isEnded(),
                workShifting.getTimeOfDay(),
                workShifting.getEmployees().stream()
                        .map(employeeDtoMapper)
                        .toList()
        );
    }
}
