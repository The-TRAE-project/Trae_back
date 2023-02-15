package ru.trae.backend.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ProjectDto(
        long id,
        String name,
        String description,
        LocalDateTime startDate,
        int period,
        boolean isEnded,
//        List<OperationDto> operations,
        ManagerDto managerDto
) {

}
