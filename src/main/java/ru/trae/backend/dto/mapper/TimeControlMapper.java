package ru.trae.backend.dto.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.trae.backend.dto.employee.ShortEmployeeDto;
import ru.trae.backend.dto.TimeControlDto;
import ru.trae.backend.entity.TimeControl;
import ru.trae.backend.entity.user.Employee;

import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class TimeControlMapper implements Function<TimeControl, TimeControlDto> {

	@Override
	public TimeControlDto apply(TimeControl tc) {
		Employee e = tc.getEmployee();

		return new TimeControlDto(tc.isOnShift(), tc.isAutoClosingShift(), tc.getArrival(), tc.getDeparture(),
				new ShortEmployeeDto(e.getId(), e.getFirstName(), e.getLastName()));
	}

}
