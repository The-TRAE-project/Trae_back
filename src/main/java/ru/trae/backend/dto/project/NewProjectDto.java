package ru.trae.backend.dto.project;

/**
 * This class represents a data transfer object (DTO) for creating a new project.
 * It contains the necessary information to create a new project, such as the number, name,
 * customer, period, order ID, and manager ID.
 *
 * @author Vladimir Olennikov
 */
public record NewProjectDto(
        long number,
        String name,
        int period,
        long managerId,
        String customer
) {
}
