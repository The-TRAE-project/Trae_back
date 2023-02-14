package ru.trae.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.trae.backend.dto.EmployeeDto;
import ru.trae.backend.entity.user.Employee;
import ru.trae.backend.service.EmployeeService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class EmployeeController {
    private final EmployeeService employeeService;

    @GetMapping("/employee/checkout/{pin}")
    public ResponseEntity<String> employeeCheckOut(@PathVariable int pin) {
        return ResponseEntity.ok(employeeService.getFirstLastName(pin));
    }

    @GetMapping("/employee/employees")
    public ResponseEntity<List<EmployeeDto>> employees() {
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }

}
