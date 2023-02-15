package ru.trae.backend.dto;

import ru.trae.backend.entity.user.Manager;

import java.time.LocalDateTime;

public record OrderDto(
        long id,
        String name,
        String description,
        LocalDateTime startDate,
        int period,
        boolean isEnded,
        CustomerDto customerDto,
        ManagerDto managerDto
) {

}
