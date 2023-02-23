package ru.trae.backend.dto.manager;

public record ManagerRegisterDto(String firstName, String middleName, String lastName, Long phone, String username,
		String email, String password) {
}
