package ru.trae.backend.dto.order;

import ru.trae.backend.dto.CustomerDto;

/**
 * Data Transfer Object for creating a new order.
 *
 * @author Vladimir Olennikov
 */
public record NewOrderDto(
        String name,
        String description,
        int period,
        long managerId,
        CustomerDto customerDto
) {
}
