package ru.trae.backend.dto.report;

import java.time.LocalDateTime;

/**
 * Data transfer object for reporting deadlines.
 *
 * @author Vladimir Olennikov
 */
public record ThirdResponseSubDto(
    long thirdRespId,
    String thirdRespValue,
    LocalDateTime plannedEndDate,
    LocalDateTime realEndDate) {
}

