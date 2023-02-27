package ru.trae.backend.dto.project;

/**
 * This class represents a data transfer object (DTO) for a project that is available for
 * an employee.
 *
 * @author Vladimir Olennikov
 */
public record ProjectAvailableForEmpDto(
        long projectId,
        long projectNumber,
        String customerLastName,
        String projectName,
        String availableOperationName
) {
}
