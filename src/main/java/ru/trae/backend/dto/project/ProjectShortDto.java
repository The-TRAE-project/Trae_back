package ru.trae.backend.dto.project;

import ru.trae.backend.dto.operation.OperationInfoForProjectTemplateDto;

/**
 * This class is a Data Transfer Object (DTO) used to encapsulate the information of a
 * short project.
 *
 * @author Vladimir Olennikov
 */
public record ProjectShortDto(
    long id,
    long number,
    String name,
    String customer,
    OperationInfoForProjectTemplateDto operation
) {
}
