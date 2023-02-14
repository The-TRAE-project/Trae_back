package ru.trae.backend.util;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.trae.backend.dto.EmployeeDto;
import ru.trae.backend.service.EmployeeService;
import ru.trae.backend.service.WorkingShiftService;

@Component
@RequiredArgsConstructor
public class CommandLineRunnerImpl implements CommandLineRunner {
    private final EmployeeService employeeService;
    private final WorkingShiftService workingShiftService;

    @Override
    public void run(String... args) {
        insertEmployees();
        insertWorkingShift();
    }

    public void insertEmployees() {
        EmployeeDto dto1 = new EmployeeDto("Иван", "Петрович", "Шилов", 89183331212L, 111);
        EmployeeDto dto2 = new EmployeeDto("Николай", "Игоревич", "Иванов", 89283332121L, 222);
        EmployeeDto dto3 = new EmployeeDto("Владимир", "Васильевич", "Петров", 89174445632L, 121);
        EmployeeDto dto4 = new EmployeeDto("Александр", "Григорьевич", "Красильников", 89271238899L, 112);
        EmployeeDto dto5 = new EmployeeDto("Никита", "Владимирович", "Бондаренко", 89153334567L, 543);
        EmployeeDto dto6 = new EmployeeDto("Валентин", "Александрович", "Плотников", 89347778294L, 764);
        EmployeeDto dto7 = new EmployeeDto("Петр", "Иванович", "Абраменко", 89183454829L, 894);
        EmployeeDto dto8 = new EmployeeDto("Григорий", "Олегович", "Костромин", 89123345993L, 253);
        employeeService.saveNewEmployee(dto1);
        employeeService.saveNewEmployee(dto2);
        employeeService.saveNewEmployee(dto3);
        employeeService.saveNewEmployee(dto4);
        employeeService.saveNewEmployee(dto5);
        employeeService.saveNewEmployee(dto6);
        employeeService.saveNewEmployee(dto7);
        employeeService.saveNewEmployee(dto8);
    }

    public void insertWorkingShift() {
        if (workingShiftService.existsActiveWorkingShift()) return;

        workingShiftService.createWorkingShift();
    }
}
