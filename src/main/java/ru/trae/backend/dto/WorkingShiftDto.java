package ru.trae.backend.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * WorkingShiftDto record is a data class that holds information about working shift.
 * It contains startShift, endShift, isEnded and list of timeControls.
 *
 * @author Vladimir Olennikov
 */
public record WorkingShiftDto(
    LocalDateTime startShift,
    LocalDateTime endShift,
    boolean isEnded,
    List<TimeControlDto> timeControls
) {
}
