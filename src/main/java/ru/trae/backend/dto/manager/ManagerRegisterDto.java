package ru.trae.backend.dto.manager;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import ru.trae.backend.util.RegExpression;

/**
 * The ManagerRegisterDto class is a data transfer object used to register a new manager.
 *
 * @author Vladimir Olennikov
 */
public record ManagerRegisterDto(
        @NotNull(message = "Invalid first name: first name is NULL")
        @Pattern(regexp = RegExpression.FIRST_MIDDLE_LAST_NAME, message = "Invalid first name")
        String firstName,
        @NotNull(message = "Invalid middle name: middle name is NULL")
        @Pattern(regexp = RegExpression.FIRST_MIDDLE_LAST_NAME, message = "Invalid middle name")
        String middleName,
        @NotNull(message = "Invalid last name: last name is NULL")
        @Pattern(regexp = RegExpression.FIRST_MIDDLE_LAST_NAME, message = "Invalid last name")
        String lastName,
        @NotNull(message = "Invalid phone number: phone number is NULL")
        @Pattern(regexp = RegExpression.PHONE_NUMBER, message = "Invalid phone number format")
        String phone,
        @NotNull(message = "Invalid username: username is NULL")
        @Pattern(regexp = RegExpression.USERNAME, message = "Invalid username format")
        String username
) {
}
