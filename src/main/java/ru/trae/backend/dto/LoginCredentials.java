package ru.trae.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents the login credentials for a user.
 *
 * @author Vladimir Olennikov
 */
public record LoginCredentials(
        @JsonProperty(value = "username", required = true)
        String username,
        @JsonProperty(value = "password", required = true)
        String password
) {
}