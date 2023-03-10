package ru.trae.backend.dto.project;

/**
 * This class is a Data Transfer Object (DTO) used to encapsulate the information of a
 * short project.
 *
 * @author Vladimir Olennikov
 */
public record ShortProjectDto(
        long id,
        long number,
        String name
) {
}
