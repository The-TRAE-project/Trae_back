package ru.trae.backend.dto.report;

import java.time.LocalDateTime;

/**
 * Sub data transfer object (third path) for reporting deadlines.
 *
 * @author Vladimir Olennikov
 */
public record ThirdResponseSubDto(
    long thirdRespId,
    String thirdRespValue,
    LocalDateTime plannedEndDate,
    LocalDateTime realEndDate) {
}

