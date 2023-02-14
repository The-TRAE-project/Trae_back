package ru.trae.backend.util;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.trae.backend.dto.EmployeeDto;
import ru.trae.backend.service.EmployeeService;

@Component
@RequiredArgsConstructor
public class CommandLineRunnerImpl implements CommandLineRunner {
    private final EmployeeService employeeService;

    @Override
    public void run(String... args) throws Exception {
        EmployeeDto dto = new EmployeeDto("Ivan", "Igorevich", "Petrov", 89995554433L, 111);
        employeeService.saveNewEmployee(dto);
    }
}
