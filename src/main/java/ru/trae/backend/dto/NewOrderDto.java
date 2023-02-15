package ru.trae.backend.dto;

public record NewOrderDto(
        String name,
        String description,
        int period,
        long managerId,
        CustomerDto customerDto
) {
}
