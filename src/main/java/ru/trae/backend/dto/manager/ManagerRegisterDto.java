package ru.trae.backend.dto.manager;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * The ManagerRegisterDto class is a data transfer object used to register a new manager.
 *
 * @author Vladimir Olennikov
 */
public record ManagerRegisterDto(
        @NotNull(message = "Invalid first name: firstName is NULL")
        @NotBlank(message = "Invalid first name: empty first name")
        @Size(min = 2, max = 15, message =
                "Invalid first name: " +
                        "the first name must be a minimum of 2 and a maximum of 15 characters")
        String firstName,
        @Size(min = 2, max = 15, message =
                "Invalid middle name: " +
                        "the middle name must be a minimum of 2 and a maximum of 15 characters")
        String middleName,
        @NotNull(message = "Invalid last name: lastName is NULL")
        @NotBlank(message = "Invalid last name: empty last name")
        @Size(min = 2, max = 15, message =
                "Invalid last name: " +
                        "the last name must be a minimum of 2 and a maximum of 15 characters")
        String lastName,
        @NotNull(message = "Invalid phone number: phone is NULL")
        @NotBlank(message = "Invalid phone number: empty number")
        @Size(min = 7, max = 30, message =
                "Invalid phone: " +
                        "the phone name must be a minimum of 7 and a maximum of 30 characters")
        String phone,
        @NotNull(message = "Invalid username: username is NULL")
        @NotBlank(message = "Invalid username: empty username")
        @Size(min = 8, max = 50, message =
                "Invalid username: " +
                        "the username name must be a minimum of 8 and a maximum of 50 characters")
        String username
) {
}
