package ru.trae.backend.dto.report;

import java.util.List;

/**
 * Sub data transfer object (second path) for reporting deadlines.
 *
 * @author Vladimir Olennikov
 */
public record SecondResponseSubDto(
    long secondRespId,
    String secondRespValue,
    List<ThirdResponseSubDto> thirdRespValues) {
}
