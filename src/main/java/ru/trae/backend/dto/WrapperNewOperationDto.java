package ru.trae.backend.dto;

import java.util.List;

public record WrapperNewOperationDto(
        long projectId,
        List<NewOperationDto> operations
) {
}
