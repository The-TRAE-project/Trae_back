package ru.trae.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record LoginCredentials(@JsonProperty(value = "username", required = true) String username,
		@JsonProperty(value = "password", required = true) String password) {
}