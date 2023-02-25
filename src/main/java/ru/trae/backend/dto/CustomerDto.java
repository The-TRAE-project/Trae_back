package ru.trae.backend.dto;

import java.time.LocalDateTime;

/**
 * Data transfer object for customers.
 *
 * @author Vladimir Olennikov
 */
public record CustomerDto(
        String firstName,
        String middleName,
        String lastName,
        Long phone,
        LocalDateTime dateOfRegister
) {
}
