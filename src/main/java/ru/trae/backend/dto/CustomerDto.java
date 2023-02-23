package ru.trae.backend.dto;

import java.time.LocalDateTime;

public record CustomerDto(String firstName, String middleName, String lastName, Long phone,
		LocalDateTime dateOfRegister) {
}
