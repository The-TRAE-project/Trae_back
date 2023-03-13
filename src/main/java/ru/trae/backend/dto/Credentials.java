package ru.trae.backend.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import com.fasterxml.jackson.annotation.JsonProperty;
import ru.trae.backend.util.RegExpression;

/**
 * Represents the login credentials for a user.
 *
 * @author Vladimir Olennikov
 */
public record Credentials(
        @NotNull(message = "Invalid username: username is NULL")
        @Pattern(regexp = RegExpression.USERNAME, message = "Invalid username format")
        @JsonProperty(value = "username", required = true)
        String username,
        @NotNull(message = "Invalid password: password is NULL")
        @Pattern(regexp = RegExpression.PASSWORD, message = "Invalid password format")
        @JsonProperty(value = "password", required = true)
        String password
) {
}