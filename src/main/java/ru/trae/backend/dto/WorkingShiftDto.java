package ru.trae.backend.dto;

import java.time.LocalDateTime;
import java.util.List;

public record WorkingShiftDto(LocalDateTime startShift, LocalDateTime endShift, boolean isEnded,
		List<TimeControlDto> timeControls) {
}
