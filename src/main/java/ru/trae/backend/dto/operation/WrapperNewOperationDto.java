package ru.trae.backend.dto.operation;

import java.util.List;

public record WrapperNewOperationDto(
        long projectId,
        List<NewOperationDto> operations
) {
}
