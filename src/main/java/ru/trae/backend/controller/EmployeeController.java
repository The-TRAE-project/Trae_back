package ru.trae.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.trae.backend.dto.CheckOutDto;
import ru.trae.backend.dto.EmployeeDto;
import ru.trae.backend.service.EmployeeService;
import ru.trae.backend.service.TimeControlService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/employee")
public class EmployeeController {
    private final EmployeeService employeeService;

    @GetMapping("/checkout/{pin}")
    public ResponseEntity<CheckOutDto> employeeCheckOut(@PathVariable int pin) {
        return ResponseEntity.ok(employeeService.checkoutEmployee(pin));
    }

    @GetMapping("/departure/{id}")
    public ResponseEntity<CheckOutDto> employeeCheckOut(@PathVariable long id) {
        return ResponseEntity.ok(employeeService.departureEmployee(id));
    }

    @GetMapping("/employees")
    public ResponseEntity<List<EmployeeDto>> employees() {
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }

}
