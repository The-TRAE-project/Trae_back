package ru.trae.backend.dto.employee;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.List;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import org.springframework.format.annotation.DateTimeFormat;
import ru.trae.backend.entity.user.Employee;
import ru.trae.backend.util.RegExpression;

/**
 * Data Transfer Object for a change data {@link Employee}.
 *
 * @author Vladimir Olennikov
 */
public record ChangeDataDtoReq(
    @Schema(description = "Идентификатор сотрудника")
    @NotNull(message = "Invalid employee id: id is NULL")
    @Min(value = 0, message = "The employee id cannot be less than 0")
    @Max(value = Integer.MAX_VALUE, message =
        "The employee id cannot be more than " + Integer.MAX_VALUE)
    long employeeId,
    @Schema(description = "Новое имя сотрудника")
    @Pattern(regexp = RegExpression.FIRST_MIDDLE_LAST_NAME, message = "Invalid first name")
    String firstName,
    @Schema(description = "Новое отчество сотрудника")
    @Pattern(regexp = RegExpression.FIRST_MIDDLE_LAST_NAME, message = "Invalid middle name")
    String middleName,
    @Schema(description = "Новая фамилия сотрудника")
    @Pattern(regexp = RegExpression.FIRST_MIDDLE_LAST_NAME, message = "Invalid last name")
    String lastName,
    @Schema(description = "Новый телефонный номер сотрудника")
    @Pattern(regexp = RegExpression.PHONE_NUMBER, message = "Invalid phone number format")
    String phone,
    @Schema(description = "Новый пин код сотрудника")
    @Min(value = 100, message = "The pin code cannot be low then 100")
    @Max(value = 999, message = "The pin code cannot be more then 999")
    Integer pinCode,
    @Schema(description = "Новый статус сотрудника")
    Boolean isActive,
    @Schema(description = "Дата принятия на работу сотрудника")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    LocalDate dateOfEmployment,
    @Schema(description = "Дата увольнения с работы сотрудника")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    LocalDate dateOfDismissal,
    @Schema(description = "Измененный список id типов работ сотрудника")
    List<Long> changedTypesId
) {
}
