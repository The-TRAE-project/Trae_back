package ru.trae.backend.dto.jwt;

/**
 * Class representing a request for refreshing a JWT (JSON Web Token).
 *
 * @author Vladimir Olennikov
 */
public record RefreshJwtRequest(
        String refreshToken
) {
}