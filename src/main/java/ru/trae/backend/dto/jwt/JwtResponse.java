package ru.trae.backend.dto.jwt;

/**
 * This class represents a response containing a set of JWT tokens.
 *
 * @author Vladimir Olennikov
 */
public record JwtResponse(
        String accessToken,
        String refreshToken
) {
}