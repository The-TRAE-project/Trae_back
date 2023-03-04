package ru.trae.backend.dto.employee;

import java.util.List;
import ru.trae.backend.entity.user.Employee;

/**
 * Data Transfer Object for a new {@link Employee}.
 *
 * @author Vladimir Olennikov
 */
public record NewEmployeeDto(
        String firstName,
        String middleName,
        String lastName,
        String phone,
        List<Long> typesId
) {
}
