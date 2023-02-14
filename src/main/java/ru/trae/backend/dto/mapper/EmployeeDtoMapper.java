package ru.trae.backend.dto.mapper;

import org.springframework.stereotype.Service;
import ru.trae.backend.dto.EmployeeDto;
import ru.trae.backend.entity.user.Employee;

import java.util.function.Function;

@Service
public class EmployeeDtoMapper implements Function<Employee, EmployeeDto> {
    @Override
    public EmployeeDto apply(Employee e) {
        return new EmployeeDto(
                e.getFirstName(),
                e.getMiddleName(),
                e.getLastName(),
                e.getPhone(),
                e.getPinCode()
        );
    }
}