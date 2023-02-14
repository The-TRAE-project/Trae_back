package ru.trae.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.trae.backend.dto.EmployeeDto;
import ru.trae.backend.entity.user.Employee;
import ru.trae.backend.repository.EmployeeRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmployeeService {
    private final EmployeeRepository employeeRepository;

    public void saveNewEmployee(EmployeeDto dto) {
        if (employeeRepository.existsByPinCode(dto.pinCode())) return;

        Employee e = new Employee();
        e.setFirstName(dto.firstName());
        e.setMiddleName(dto.middleName());
        e.setLastName(dto.lastName());
        e.setPhone(dto.phone());
        e.setPinCode(dto.pinCode());

        employeeRepository.save(e);
    }

    public ResponseEntity<String> getFirstLastName(int pin) {
        Optional<Employee> employee = employeeRepository.findByPinCode(pin);
        return employee.map(value -> ResponseEntity.ok(value.getFirstName() + " " + value.getLastName()))
                .orElseGet(() -> new ResponseEntity<>("Работник с пинкодом " + pin + " не найден", HttpStatus.NOT_FOUND));
    }


}
