package ru.trae.backend.dto.order;

import ru.trae.backend.dto.CustomerDto;

public record NewOrderDto(String name, String description, int period, long managerId, CustomerDto customerDto) {
}
