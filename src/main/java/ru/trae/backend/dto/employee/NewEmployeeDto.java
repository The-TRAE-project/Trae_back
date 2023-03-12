package ru.trae.backend.dto.employee;

import java.util.List;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import ru.trae.backend.entity.user.Employee;

/**
 * Data Transfer Object for a new {@link Employee}.
 *
 * @author Vladimir Olennikov
 */
public record NewEmployeeDto(
        @NotNull(message = "Invalid first name: first name is NULL")
        @Pattern(regexp = "^[А-Я]-?[а-я]{1,15}$", message = "Invalid first name")
        String firstName,
        @NotNull(message = "Invalid middle name: middle name is NULL")
        @Pattern(regexp = "^[А-Я]-?[а-я]{1,15}$", message = "Invalid middle name")
        String middleName,
        @NotNull(message = "Invalid last name: last name is NULL")
        @Pattern(regexp = "^[А-Я]-?[а-я]{1,15}$", message = "Invalid last name")
        String lastName,
        @NotNull(message = "Invalid phone number: phone number is NULL")
        @Pattern(regexp =
                "^(\\+\\d{1,3}\\s?)?1?\\-?\\.?\\s?\\(?\\d{3}\\)?[\\s.-]?\\d{3}[\\s.-]?\\d{4}$",
                message = "Invalid phone number format")
        String phone,
        @NotNull(message = "Invalid types work: types work list is NULL")
        List<Long> typesId
) {
}
