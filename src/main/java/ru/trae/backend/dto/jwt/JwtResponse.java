package ru.trae.backend.dto.jwt;

public record JwtResponse(
        String accessToken,
        String refreshToken
) {
}