package ru.trae.backend.dto.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.trae.backend.dto.TimeControlDto;
import ru.trae.backend.entity.TimeControl;

import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class TimeControlMapper implements Function<TimeControl, TimeControlDto> {
    private final EmployeeDtoMapper employeeDtoMapper;

    @Override
    public TimeControlDto apply(TimeControl tc) {
        return new TimeControlDto(
                tc.isOnShift(),
                tc.getArrival(),
                tc.getDeparture(),
                employeeDtoMapper.apply(tc.getEmployee())
        );
    }
}
