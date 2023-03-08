package ru.trae.backend.dto.project;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import ru.trae.backend.dto.operation.NewOperationDto;

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
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        LocalDateTime plannedEndDate,
        String customer,
        String comment,
        List<NewOperationDto> operations
) {
}
