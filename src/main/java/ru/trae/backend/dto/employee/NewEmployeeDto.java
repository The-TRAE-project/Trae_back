package ru.trae.backend.dto.employee;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.List;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import org.springframework.format.annotation.DateTimeFormat;
import ru.trae.backend.entity.user.Employee;
import ru.trae.backend.util.RegExpression;

/**
 * Data Transfer Object for a new {@link Employee}.
 *
 * @author Vladimir Olennikov
 */
public record NewEmployeeDto(
    @NotNull(message = "Invalid first name: first name is NULL")
    @Pattern(regexp = RegExpression.FIRST_MIDDLE_LAST_NAME, message = "Invalid first name")
    String firstName,
    @Pattern(regexp = RegExpression.FIRST_MIDDLE_LAST_NAME, message = "Invalid middle name")
    String middleName,
    @NotNull(message = "Invalid last name: last name is NULL")
    @Pattern(regexp = RegExpression.FIRST_MIDDLE_LAST_NAME, message = "Invalid last name")
    String lastName,
    @NotNull(message = "Invalid phone number: phone number is NULL")
    @Pattern(regexp = RegExpression.PHONE_NUMBER, message = "Invalid phone number format")
    String phone,
    @Schema(description = "Дата принятия на работу пользователя")
    @NotNull(message = "Invalid date of employment: date is NULL")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    LocalDate dateOfEmployment,
    @NotNull(message = "Invalid types work: types work list is NULL")
    List<Long> typesId
) {
}
