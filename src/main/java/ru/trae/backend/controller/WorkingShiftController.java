package ru.trae.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.trae.backend.dto.WorkingShiftDto;
import ru.trae.backend.service.WorkingShiftService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/working-shift")
public class WorkingShiftController {
    private final WorkingShiftService workingShiftService;

    @GetMapping("/active")
    public ResponseEntity<WorkingShiftDto> getActiveWorkingShift() {
        return ResponseEntity.ok(workingShiftService.getActive());
    }

    @GetMapping("/on-shift/{pinCode}")
    public ResponseEntity<Boolean> getStatusEmployee(@PathVariable int pinCode) {
        return ResponseEntity.ok(workingShiftService.isEmployeeOnShift(pinCode));
    }
}
