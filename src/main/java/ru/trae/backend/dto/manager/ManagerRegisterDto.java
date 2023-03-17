package ru.trae.backend.dto.manager;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import org.springframework.format.annotation.DateTimeFormat;
import ru.trae.backend.util.RegExpression;

/**
 * The ManagerRegisterDto class is a data transfer object used to register a new manager.
 *
 * @author Vladimir Olennikov
 */
public record ManagerRegisterDto(
    @Schema(description = "Имя пользователя")
    @NotNull(message = "Invalid first name: first name is NULL")
    @Pattern(regexp = RegExpression.FIRST_MIDDLE_LAST_NAME, message = "Invalid first name")
    String firstName,
    @Schema(description = "Отчество пользователя")
    @NotNull(message = "Invalid middle name: middle name is NULL")
    @Pattern(regexp = RegExpression.FIRST_MIDDLE_LAST_NAME, message = "Invalid middle name")
    String middleName,
    @Schema(description = "Фамилия пользователя")
    @NotNull(message = "Invalid last name: last name is NULL")
    @Pattern(regexp = RegExpression.FIRST_MIDDLE_LAST_NAME, message = "Invalid last name")
    String lastName,
    @Schema(description = "Номер телефона пользователя")
    @NotNull(message = "Invalid phone number: phone number is NULL")
    @Pattern(regexp = RegExpression.PHONE_NUMBER, message = "Invalid phone number format")
    String phone,
    @Schema(description = "Юзернейм(логин) пользователя")
    @NotNull(message = "Invalid username: username is NULL")
    @Pattern(regexp = RegExpression.USERNAME, message = "Invalid username format")
    String username,
    @Schema(description = "Дата принятия на работу пользователя")
    @NotNull(message = "Invalid date of employment: date is NULL")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    LocalDateTime dateOfEmployment
) {
}
