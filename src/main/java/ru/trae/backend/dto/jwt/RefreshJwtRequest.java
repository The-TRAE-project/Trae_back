package ru.trae.backend.dto.jwt;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import ru.trae.backend.util.RegExpression;

/**
 * Class representing a request for refreshing a JWT (JSON Web Token).
 *
 * @author Vladimir Olennikov
 */
public record RefreshJwtRequest(
        @NotNull(message = "Invalid token: token is NULL")
        @Pattern(regexp = RegExpression.TOKEN, message = "Invalid token format")
        String refreshToken
) {
}