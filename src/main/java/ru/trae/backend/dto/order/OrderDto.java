package ru.trae.backend.dto.order;

import ru.trae.backend.dto.CustomerDto;
import ru.trae.backend.dto.manager.ManagerDto;

import java.time.LocalDateTime;

public record OrderDto(long id, String name, String description, LocalDateTime startDate, LocalDateTime plannedEndDate,
		LocalDateTime realEndDate, int period, boolean isEnded, CustomerDto customerDto, ManagerDto managerDto) {

}
