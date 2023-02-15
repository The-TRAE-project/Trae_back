package ru.trae.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.trae.backend.dto.ShortEmployeeDto;
import ru.trae.backend.dto.EmployeeDto;
import ru.trae.backend.service.EmployeeService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/employee")
public class EmployeeController {
    private final EmployeeService employeeService;

    @GetMapping("/checkin/{pin}")
    public ResponseEntity<ShortEmployeeDto> employeeCheckOut(@PathVariable int pin) {
        return ResponseEntity.ok(employeeService.checkoutEmployee(pin));
    }

    @GetMapping("/checkout/{id}")
    public ResponseEntity<ShortEmployeeDto> employeeCheckOut(@PathVariable long id) {
        return ResponseEntity.ok(employeeService.departureEmployee(id));
    }

    @GetMapping("/employees")
    public ResponseEntity<List<EmployeeDto>> employees() {
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }

}
