package ru.trae.backend.dto.operation;

import java.util.List;

/**
 * This class is a wrapper for the {@link NewOperationDto} class. It contains a project id and a
 * list of {@link NewOperationDto} objects.
 *
 * @author Vladimir Olennikov
 */
public record WrapperNewOperationDto(
        long projectId,
        List<NewOperationDto> operations
) {
}
