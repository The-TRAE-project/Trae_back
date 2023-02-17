package ru.trae.backend.util;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.trae.backend.dto.CustomerDto;
import ru.trae.backend.dto.type.NewTypeWorkDto;
import ru.trae.backend.dto.employee.NewEmployeeDto;
import ru.trae.backend.dto.manager.ManagerRegisterDto;
import ru.trae.backend.dto.order.NewOrderDto;
import ru.trae.backend.dto.project.NewProjectDto;
import ru.trae.backend.service.*;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CommandLineRunnerImpl implements CommandLineRunner {
    private final EmployeeService employeeService;
    private final ManagerService managerService;
    private final ProjectService projectService;
    private final OrderService orderService;
    private final TypeWorkService typeWorkService;

    @Override
    public void run(String... args) {
        insertTypeWork();
        insertEmployees();
        insertManager();
        insertOrder();
        insertProject();
    }

    public void insertTypeWork() {
        List<NewTypeWorkDto> list = List.of(
                new NewTypeWorkDto("Раскрой"),
                new NewTypeWorkDto("Кромка"),
                new NewTypeWorkDto("Присадка"),
                new NewTypeWorkDto("Фрезеровка"),
                new NewTypeWorkDto("Склейка"),
                new NewTypeWorkDto("Сборка"),
                new NewTypeWorkDto("Шлифовка/покраска"),
                new NewTypeWorkDto("Упаковка"),
                new NewTypeWorkDto("Отгрузка"));

        list.stream()
                .filter(t -> !typeWorkService.existsTypeByName(t.name()))
                .forEach(typeWorkService::saveNewTypeWork);
    }

    public void insertEmployees() {
        List<NewEmployeeDto> list = List.of(
                new NewEmployeeDto("Иван", "Петрович", "Шилов", 89183331212L, List.of(1L, 2L, 3L, 4L, 5L, 6L, 7L)),
                new NewEmployeeDto("Николай", "Игоревич", "Иванов", 89283332121L, List.of(1L, 2L, 3L, 4L, 5L, 6L, 7L)),
                new NewEmployeeDto("Владимир", "Васильевич", "Петров", 89174445632L, List.of(8L)),
                new NewEmployeeDto("Александр", "Григорьевич", "Красильников", 89271238899L, List.of(8L)),
                new NewEmployeeDto("Никита", "Владимирович", "Бондаренко", 89153334567L, List.of(1L)),
                new NewEmployeeDto("Валентин", "Александрович", "Плотников", 89347778294L, List.of(1L, 5L, 6L, 7L)),
                new NewEmployeeDto("Петр", "Иванович", "Абраменко", 89183454829L, List.of(1L, 2L, 3L, 4L)),
                new NewEmployeeDto("Григорий", "Олегович", "Костромин", 89123345993L, List.of(4L, 5L, 6L, 7L)),
                new NewEmployeeDto("Егор", "Антонович", "Карпов", 89155675993L, List.of(1L, 2L, 5L, 6L, 7L)),
                new NewEmployeeDto("Антон", "Петрович", "Рыбин", 89132245911L, List.of(1L, 2L, 3L, 4L, 5L, 6L, 7L)),
                new NewEmployeeDto("Аркадий", "Олегович", "Павлов", 89113335798L, List.of(1L, 2L, 3L, 4L, 5L)),
                new NewEmployeeDto("Степан", "Степанович", "Никитин", 89223245913L, List.of(1L, 2L, 5L, 6L, 7L, 8L))
        );

        list.stream()
                .filter(e -> !employeeService.existsByCredentials(e.firstName(), e.middleName(), e.lastName()))
                .forEach(employeeService::saveNewEmployee);
    }

    public void insertManager() {
        ManagerRegisterDto dto = new ManagerRegisterDto("Михаил", "Михаилович", "Мишин", 89991112233L,
                "man", "man@mail.ru", "1234");

        if (!managerService.existsManagerByEmail(dto.email()))
            managerService.saveNewManager(dto);
    }

    public void insertOrder() {
        if (orderService.getAllOrder().size() == 0) {
            NewOrderDto dto = new NewOrderDto(
                    "Нужна красивая тумбочка, чтобы поставить в спальне",
                    "Размер 50х60х70 см, покрасить в черный цвет, покрыть лаком, 3 ящика",
                    50,
                    1L,
                    new CustomerDto(
                            "Олег", "Александрович", "Сидоров", 89125548722L));

            orderService.receiveNewOrder(dto);
        }
    }

    public void insertProject() {
        if (projectService.getAllProjects().size() == 0) {
            NewProjectDto dto = new NewProjectDto(
                    "Прикроватная тумбочка",
                    "Здесь должно быть правильное описание, пояснение, указания и тд",
                    40,
                    1L);

            projectService.saveNewProject(dto);
        }

    }
}
