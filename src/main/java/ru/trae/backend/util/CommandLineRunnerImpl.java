package ru.trae.backend.util;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.trae.backend.dto.EmployeeDto;
import ru.trae.backend.dto.ManagerRegisterDto;
import ru.trae.backend.service.EmployeeService;
import ru.trae.backend.service.ManagerService;
import ru.trae.backend.service.WorkingShiftService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CommandLineRunnerImpl implements CommandLineRunner {
    private final EmployeeService employeeService;
    private final WorkingShiftService workingShiftService;
    private final ManagerService managerService;

    @Override
    public void run(String... args) {
        insertEmployees();
        insertWorkingShift();
        insertManager();
    }

    public void insertEmployees() {
        List<EmployeeDto> list = List.of(
                new EmployeeDto(null, "Иван", "Петрович", "Шилов", 89183331212L, 111),
                new EmployeeDto(null, "Николай", "Игоревич", "Иванов", 89283332121L, 222),
                new EmployeeDto(null, "Владимир", "Васильевич", "Петров", 89174445632L, 121),
                new EmployeeDto(null, "Александр", "Григорьевич", "Красильников", 89271238899L, 112),
                new EmployeeDto(null, "Никита", "Владимирович", "Бондаренко", 89153334567L, 543),
                new EmployeeDto(null, "Валентин", "Александрович", "Плотников", 89347778294L, 764),
                new EmployeeDto(null, "Петр", "Иванович", "Абраменко", 89183454829L, 894),
                new EmployeeDto(null, "Григорий", "Олегович", "Костромин", 89123345993L, 253),
                new EmployeeDto(null, "Егор", "Антонович", "Карпов", 89155675993L, 113),
                new EmployeeDto(null, "Антон", "Петрович", "Рыбин", 89132245911L, 115),
                new EmployeeDto(null, "Аркадий", "Олегович", "Павлов", 89113335798L, 122),
                new EmployeeDto(null, "Степан", "Степанович", "Никитин", 89223245913L, 134)
        );

        list.stream()
                .filter(e -> !employeeService.existsEmpByPinCode(e.pinCode()))
                .forEach(employeeService::saveNewEmployee);
    }

    public void insertWorkingShift() {
        if (workingShiftService.existsActiveWorkingShift()) return;

        workingShiftService.createWorkingShift();
    }

    public void insertManager() {
        ManagerRegisterDto dto = new ManagerRegisterDto("Михаил", "Михаилович", "Мишин", 89991112233L,
                "man", "man@mail.ru", "1234");

        if (!managerService.existsManagerByEmail(dto.email()))
            managerService.saveNewManager(dto);
    }
}
