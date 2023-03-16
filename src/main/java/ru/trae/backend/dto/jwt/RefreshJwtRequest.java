package ru.trae.backend.dto.jwt;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import ru.trae.backend.util.RegExpression;

/**
 * Class representing a request for refreshing a JWT (JSON Web Token).
 *
 * @author Vladimir Olennikov
 */
public record RefreshJwtRequest(
    @Schema(description = "Рефреш токен")
    @NotNull(message = "Invalid token: token is NULL")
    @Pattern(regexp = RegExpression.TOKEN, message = "Invalid token format")
    String refreshToken
) {
}