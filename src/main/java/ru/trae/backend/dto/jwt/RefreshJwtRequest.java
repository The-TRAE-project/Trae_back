package ru.trae.backend.dto.jwt;

public record RefreshJwtRequest(
        String refreshToken
) {
}