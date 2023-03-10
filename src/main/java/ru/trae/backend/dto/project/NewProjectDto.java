package ru.trae.backend.dto.project;

import java.time.LocalDateTime;
import java.util.List;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;
import ru.trae.backend.dto.operation.NewOperationDto;
import ru.trae.backend.util.RegExpression;

/**
 * This class represents a data transfer object (DTO) for creating a new project.
 * It contains the necessary information to create a new project, such as the number, name,
 * customer, period, order ID, and manager ID.
 *
 * @author Vladimir Olennikov
 */
public record NewProjectDto(
        //todo to complete this field
        long number,
        @NotNull(message = "Invalid name: name is NULL")
        @Pattern(regexp = RegExpression.PROJECT_NAME, message = "Invalid name format")
        String name,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        LocalDateTime plannedEndDate,
        @NotNull(message = "Invalid customer: customer is NULL")
        @Pattern(regexp = RegExpression.CUSTOMER, message = "Invalid customer format")
        String customer,
        @Size(max = 1000, message = "The comment cannot be more than 1000 symbols")
        String comment,
        @NotNull(message = "Invalid operations: operations list is NULL")
        List<NewOperationDto> operations
) {
}
