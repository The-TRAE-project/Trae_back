package ru.trae.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.trae.backend.dto.ShortEmployeeDto;
import ru.trae.backend.dto.EmployeeDto;
import ru.trae.backend.entity.user.Employee;
import ru.trae.backend.service.EmployeeService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/employee")
public class EmployeeController {
    private final EmployeeService employeeService;

    @GetMapping("/checkin/{pin}")
    public ResponseEntity<ShortEmployeeDto> employeeCheckIn(@PathVariable int pin) {
        return ResponseEntity.ok(employeeService.checkInEmployee(pin));
    }

    @GetMapping("/checkout/{id}")
    public ResponseEntity<ShortEmployeeDto> employeeCheckOut(@PathVariable long id) {
        return ResponseEntity.ok(employeeService.departureEmployee(id));
    }

    @GetMapping("/employees")
    public ResponseEntity<List<EmployeeDto>> employees() {
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }

    @PostMapping("/register")
    public ResponseEntity<EmployeeDto> register(@RequestBody EmployeeDto dto) {
        employeeService.checkAvailablePinCode(dto.pinCode());
        Employee e = employeeService.saveNewEmployee(dto);
        return ResponseEntity.ok(employeeService.getEmpDtoById(e.getId()));
    }
}
