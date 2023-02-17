package ru.trae.backend.dto.mapper;

import org.springframework.stereotype.Service;
import ru.trae.backend.dto.employee.EmployeeDto;
import ru.trae.backend.entity.TypeWork;
import ru.trae.backend.entity.user.Employee;

import java.util.function.Function;

@Service
public class EmployeeDtoMapper implements Function<Employee, EmployeeDto> {
    @Override
    public EmployeeDto apply(Employee e) {
        return new EmployeeDto(
                e.getId(),
                e.getFirstName(),
                e.getMiddleName(),
                e.getLastName(),
                e.getPhone(),
                e.getPinCode(),
                e.getTypeWorks().stream()
                        .map(TypeWork::getName)
                        .toList()
        );
    }
}
