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
	public ResponseEntity<WorkingShiftDto> activeWorkingShift() {
		return ResponseEntity.ok(workingShiftService.getActive());
	}

	@GetMapping("/on-shift/{id}")
	public ResponseEntity<Boolean> statusEmployee(@PathVariable int id) {
		return ResponseEntity.ok(workingShiftService.employeeOnShift(true, id));
	}

}
