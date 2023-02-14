package ru.trae.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.trae.backend.dto.WorkShiftingDto;
import ru.trae.backend.service.WorkShiftingService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/work-shifting")
public class WorkShiftingController {
    private final WorkShiftingService workShiftingService;

    @GetMapping("/active")
    public ResponseEntity<WorkShiftingDto> getActiveWorkShifting() {
        return ResponseEntity.ok(workShiftingService.getActive());
    }

}
