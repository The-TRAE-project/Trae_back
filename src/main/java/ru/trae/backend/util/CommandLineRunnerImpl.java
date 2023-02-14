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
        EmployeeDto dto1 = new EmployeeDto(null,"Иван", "Петрович", "Шилов", 89183331212L, 111);
        EmployeeDto dto2 = new EmployeeDto(null,"Николай", "Игоревич", "Иванов", 89283332121L, 222);
        EmployeeDto dto3 = new EmployeeDto(null,"Владимир", "Васильевич", "Петров", 89174445632L, 121);
        EmployeeDto dto4 = new EmployeeDto(null,"Александр", "Григорьевич", "Красильников", 89271238899L, 112);
        EmployeeDto dto5 = new EmployeeDto(null,"Никита", "Владимирович", "Бондаренко", 89153334567L, 543);
        EmployeeDto dto6 = new EmployeeDto(null,"Валентин", "Александрович", "Плотников", 89347778294L, 764);
        EmployeeDto dto7 = new EmployeeDto(null,"Петр", "Иванович", "Абраменко", 89183454829L, 894);
        EmployeeDto dto8 = new EmployeeDto(null,"Григорий", "Олегович", "Костромин", 89123345993L, 253);
        EmployeeDto dto9 = new EmployeeDto(null,"Егор", "Антонович", "Карпов", 89155675993L, 113);
        EmployeeDto dto10 = new EmployeeDto(null,"Антон", "Петрович", "Рыбин", 89132245911L, 115);
        EmployeeDto dto11 = new EmployeeDto(null,"Аркадий", "Олегович", "Павлов", 89113335798L, 122);
        EmployeeDto dto12 = new EmployeeDto(null,"Степан", "Степанович", "Никитин", 89223245913L, 134);
        employeeService.saveNewEmployee(dto1);
        employeeService.saveNewEmployee(dto2);
        employeeService.saveNewEmployee(dto3);
        employeeService.saveNewEmployee(dto4);
        employeeService.saveNewEmployee(dto5);
        employeeService.saveNewEmployee(dto6);
        employeeService.saveNewEmployee(dto7);
        employeeService.saveNewEmployee(dto8);
        employeeService.saveNewEmployee(dto9);
        employeeService.saveNewEmployee(dto10);
        employeeService.saveNewEmployee(dto11);
        employeeService.saveNewEmployee(dto12);
    }

    public void insertWorkingShift() {
        if (workingShiftService.existsActiveWorkingShift()) return;

        workingShiftService.createWorkingShift();
    }
}
