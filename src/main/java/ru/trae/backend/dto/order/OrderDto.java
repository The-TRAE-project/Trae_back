package ru.trae.backend.dto.order;

import java.time.LocalDateTime;
import ru.trae.backend.dto.CustomerDto;
import ru.trae.backend.dto.manager.ManagerDto;

/**
 * Data Transfer Object for Order.
 *
 * @author Vladimir Olennikov
 */
public record OrderDto(
        long id,
        String name,
        String description,
        LocalDateTime startDate,
        LocalDateTime plannedEndDate,
        LocalDateTime realEndDate,
        int period,
        boolean isEnded,
        CustomerDto customerDto,
        ManagerDto managerDto
) {
}
