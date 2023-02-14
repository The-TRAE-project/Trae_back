package ru.trae.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.trae.backend.service.EmployeeService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class EmployeeController {
    private final EmployeeService employeeService;

    @GetMapping("/employee/{pin}")
    public ResponseEntity<String> employeeCheckOut(@PathVariable int pin) {
        return employeeService.getFirstLastName(pin);
    }

}
