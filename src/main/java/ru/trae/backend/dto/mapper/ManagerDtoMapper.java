package ru.trae.backend.dto.mapper;

import org.springframework.stereotype.Service;
import ru.trae.backend.dto.manager.ManagerDto;
import ru.trae.backend.entity.user.Manager;

import java.time.format.DateTimeFormatter;
import java.util.function.Function;

@Service
public class ManagerDtoMapper implements Function<Manager, ManagerDto> {

	@Override
	public ManagerDto apply(Manager m) {
		return new ManagerDto(m.getId(), m.getFirstName(), m.getMiddleName(), m.getLastName(), m.getPhone(),
				m.getEmail(), m.getRole().value,
				m.getDateOfRegister().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
	}

}
