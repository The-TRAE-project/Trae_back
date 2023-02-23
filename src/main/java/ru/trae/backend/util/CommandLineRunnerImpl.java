package ru.trae.backend.util;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.trae.backend.dto.CustomerDto;
import ru.trae.backend.dto.operation.NewOperationDto;
import ru.trae.backend.dto.operation.OperationDto;
import ru.trae.backend.dto.operation.WrapperNewOperationDto;
import ru.trae.backend.dto.type.NewTypeWorkDto;
import ru.trae.backend.dto.employee.NewEmployeeDto;
import ru.trae.backend.dto.manager.ManagerRegisterDto;
import ru.trae.backend.dto.order.NewOrderDto;
import ru.trae.backend.dto.project.NewProjectDto;
import ru.trae.backend.entity.task.Operation;
import ru.trae.backend.entity.task.Project;
import ru.trae.backend.service.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CommandLineRunnerImpl implements CommandLineRunner {

	private final EmployeeService employeeService;

	private final ManagerService managerService;

	private final ProjectService projectService;

	private final OrderService orderService;

	private final TypeWorkService typeWorkService;

	private final OperationService operationService;

	@Override
	public void run(String... args) {
		insertTypeWork();
		insertEmployees();
		insertManager();
		insertOrder();
		insertProject();
		insertOperations();
	}

	public void insertTypeWork() {
		List<NewTypeWorkDto> list = List.of(new NewTypeWorkDto("Раскрой"), new NewTypeWorkDto("Кромка"),
				new NewTypeWorkDto("Присадка"), new NewTypeWorkDto("Фрезеровка"), new NewTypeWorkDto("Склейка"),
				new NewTypeWorkDto("Сборка"), new NewTypeWorkDto("Шлифовка/покраска"), new NewTypeWorkDto("Упаковка"),
				new NewTypeWorkDto("Отгрузка"));

		list.stream()
			.filter(t -> !typeWorkService.existsTypeByName(t.name()))
			.forEach(typeWorkService::saveNewTypeWork);
	}

	public void insertEmployees() {
		List<NewEmployeeDto> list = List.of(
				new NewEmployeeDto("Иван", "Петрович", "Шилов", 89183331212L, List.of(1L, 2L, 3L, 4L, 5L, 6L, 7L)),
				new NewEmployeeDto("Николай", "Игоревич", "Иванов", 89283332121L, List.of(3L, 4L, 5L, 6L, 7L)),
				new NewEmployeeDto("Владимир", "Васильевич", "Петров", 89174445632L, List.of(8L, 9L)),
				new NewEmployeeDto("Александр", "Григорьевич", "Красильников", 89271238899L, List.of(8L, 9L)),
				new NewEmployeeDto("Никита", "Владимирович", "Бондаренко", 89153334567L, List.of(1L)),
				new NewEmployeeDto("Валентин", "Александрович", "Плотников", 89347778294L, List.of(1L, 5L, 6L, 7L)),
				new NewEmployeeDto("Петр", "Иванович", "Абраменко", 89183454829L, List.of(1L, 2L, 3L, 4L)),
				new NewEmployeeDto("Григорий", "Олегович", "Костромин", 89123345993L, List.of(4L, 5L, 6L, 7L)),
				new NewEmployeeDto("Егор", "Антонович", "Карпов", 89155675993L, List.of(1L, 2L, 5L, 6L, 7L)),
				new NewEmployeeDto("Антон", "Петрович", "Рыбин", 89132245911L, List.of(1L, 2L, 3L, 4L, 5L, 6L, 7L)),
				new NewEmployeeDto("Аркадий", "Олегович", "Павлов", 89113335798L, List.of(1L, 2L, 3L, 4L, 5L)),
				new NewEmployeeDto("Степан", "Степанович", "Никитин", 89223245913L,
						List.of(1L, 2L, 5L, 6L, 7L, 8L, 9L)));

		list.stream()
			.filter(e -> !employeeService.existsByCredentials(e.firstName(), e.middleName(), e.lastName()))
			.forEach(employeeService::saveNewEmployee);
	}

	public void insertManager() {
		ManagerRegisterDto dto = new ManagerRegisterDto("Михаил", "Михаилович", "Мишин", 89991112233L, "man",
				"man@mail.ru", "1234");

		if (!managerService.existsManagerByEmail(dto.email()))
			managerService.saveNewManager(dto);
	}

	public void insertOrder() {
		if (orderService.getAllOrder().size() == 0) {
			NewOrderDto dto1 = new NewOrderDto("Платяной шкаф в спальню",
					"Размер 220х100х70 см, покрасить в белый цвет", 50, 1L,
					new CustomerDto("Олег", "Александрович", "Сидоров", 89125548722L, LocalDateTime.now()));

			NewOrderDto dto2 = new NewOrderDto("Входная дверь",
					"Размер 200х200х30 см, без покраски, только покрыть лаком", 50, 1L,
					new CustomerDto("Андрей", "Владимирович", "Никифоров", 89115437820L, LocalDateTime.now()));

			NewOrderDto dto3 = new NewOrderDto("Стол в мастерскую", "Размер 500х200х100 см, покрасить в черный цвет",
					50, 1L, new CustomerDto("Ольга", "Сергеевна", "Иванова", 89515545612L, LocalDateTime.now()));

			orderService.receiveNewOrder(dto1);
			orderService.receiveNewOrder(dto2);
			orderService.receiveNewOrder(dto3);
		}
	}

	public void insertProject() {
		if (projectService.getAllProjects().size() == 0) {
			NewProjectDto dto1 = new NewProjectDto("Шкаф",
					"Здесь должно быть правильное описание, пояснение, указания и тд", 40, 1L, 1L);

			NewProjectDto dto2 = new NewProjectDto("Дверь",
					"Здесь должно быть правильное описание, пояснение, указания и тд", 40, 2L, 1L);

			NewProjectDto dto3 = new NewProjectDto("Стол",
					"Здесь должно быть правильное описание, пояснение, указания и тд", 40, 3L, 1L);

			projectService.saveNewProject(dto1);
			projectService.saveNewProject(dto2);
			projectService.saveNewProject(dto3);
		}
	}

	public void insertOperations() {
		Project p1 = projectService.getProjectById(1);
		Project p2 = projectService.getProjectById(2);
		Project p3 = projectService.getProjectById(3);

		if (p1.getOperations().isEmpty()) {
			List<NewOperationDto> operations = new ArrayList<>();
			operations.add(new NewOperationDto("Раскрой", "Инструкции по раскрою", 1, 10));
			operations.add(new NewOperationDto("Кромка", "Инструкции по кромке", 2, 20));
			operations.add(new NewOperationDto("Фрезеровка", "Инструкции по фрезеровке", 4, 30));
			operations.add(new NewOperationDto("Присадка", "Инструкции по присадке", 3, 40));
			operations.add(new NewOperationDto("Сборка", "Инструкции по сборке", 6, 50));
			operations.add(new NewOperationDto("Покраска", "Инструкции по покраске", 7, 60));
			operations.add(new NewOperationDto("Отгрузка", "Инструкции по отгрузке", 9, 70));
			WrapperNewOperationDto wrapper = new WrapperNewOperationDto(p1.getId(), operations);
			operationService.saveNewOperations(wrapper);
		}

		if (p2.getOperations().isEmpty()) {
			List<NewOperationDto> operations = new ArrayList<>();
			operations.add(new NewOperationDto("Раскрой", "Инструкции по раскрою", 1, 10));
			operations.add(new NewOperationDto("Кромка", "Инструкции по кромке", 2, 20));
			operations.add(new NewOperationDto("Фрезеровка", "Инструкции по фрезеровке", 4, 30));
			operations.add(new NewOperationDto("Сборка", "Инструкции по сборке", 6, 40));
			operations
				.add(new NewOperationDto("Особый вид покраски - лакировка", "Инструкции по покрытию лаком", 7, 50));
			operations.add(new NewOperationDto("Отгрузка", "Инструкции по отгрузке", 9, 60));
			WrapperNewOperationDto wrapper = new WrapperNewOperationDto(p2.getId(), operations);
			operationService.saveNewOperations(wrapper);
		}

		if (p3.getOperations().isEmpty()) {
			List<NewOperationDto> operations = new ArrayList<>();
			operations.add(new NewOperationDto("Раскрой", "Инструкции по раскрою", 1, 10));
			operations.add(new NewOperationDto("Кромка", "Инструкции по кромке", 2, 20));
			operations.add(new NewOperationDto("Фрезеровка", "Инструкции по фрезеровке", 4, 30));
			operations.add(new NewOperationDto("Сборка", "Инструкции по сборке", 6, 40));
			operations.add(new NewOperationDto("Покраска", "Инструкции по покраске, 1-ый слой", 7, 50));
			operations.add(new NewOperationDto("Покраска", "Инструкции по покраске, 2-ой слой", 7, 60));
			operations.add(new NewOperationDto("Отгрузка", "Инструкции по отгрузке", 9, 70));
			WrapperNewOperationDto wrapper = new WrapperNewOperationDto(p3.getId(), operations);
			operationService.saveNewOperations(wrapper);
		}
	}

}
