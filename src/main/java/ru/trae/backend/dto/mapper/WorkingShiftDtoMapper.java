package ru.trae.backend.dto.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.trae.backend.dto.WorkingShiftDto;
import ru.trae.backend.entity.WorkingShift;

import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class WorkingShiftDtoMapper implements Function<WorkingShift, WorkingShiftDto> {

    private final EmployeeDtoMapper employeeDtoMapper;

    @Override
    public WorkingShiftDto apply(WorkingShift workingShift) {
        return new WorkingShiftDto(
                workingShift.getStartShift(),
                workingShift.getEndShift(),
                workingShift.isEnded(),
                workingShift.getTimeOfDay(),
                workingShift.getEmployees().stream()
                        .map(employeeDtoMapper)
                        .toList()
        );
    }
}
