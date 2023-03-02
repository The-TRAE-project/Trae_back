/*
 * Copyright (c) 2023. Vladimir Olennikov.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.trae.backend.util;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.trae.backend.dto.employee.NewEmployeeDto;
import ru.trae.backend.dto.manager.ManagerRegisterDto;
import ru.trae.backend.dto.operation.NewOperationDto;
import ru.trae.backend.dto.operation.WrapperNewOperationDto;
import ru.trae.backend.dto.project.NewProjectDto;
import ru.trae.backend.dto.type.NewTypeWorkDto;
import ru.trae.backend.entity.task.Project;
import ru.trae.backend.service.EmployeeService;
import ru.trae.backend.service.ManagerService;
import ru.trae.backend.service.OperationService;
import ru.trae.backend.service.ProjectService;
import ru.trae.backend.service.TypeWorkService;

/**
 * Utility class for filling the database with temporary data.
 *
 * @author Vladimir Olennikov
 */
@Component
@RequiredArgsConstructor
public class CommandLineRunnerImpl implements CommandLineRunner {
  private final EmployeeService employeeService;
  private final ManagerService managerService;
  private final ProjectService projectService;
  private final TypeWorkService typeWorkService;
  private final OperationService operationService;

  @Override
  public void run(String... args) {
    insertTypeWork();
    insertEmployees();
    insertManager();
    insertProject();
    insertOperations();
  }

  /**
   * Inserting data types of work.
   */
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

  /**
   * Inserting employee data.
   */
  public void insertEmployees() {
    List<NewEmployeeDto> list = List.of(
            new NewEmployeeDto("Иван", "Петрович", "Шилов",
                    89183331212L, List.of(1L, 2L, 3L, 4L, 5L, 6L, 7L)),
            new NewEmployeeDto("Николай", "Игоревич", "Иванов",
                    89283332121L, List.of(3L, 4L, 5L, 6L, 7L)),
            new NewEmployeeDto("Владимир", "Васильевич", "Петров",
                    89174445632L, List.of(8L, 9L)),
            new NewEmployeeDto("Александр", "Григорьевич",
                    "Красильников", 89271238899L, List.of(8L, 9L)),
            new NewEmployeeDto("Никита", "Владимирович", "Бондаренко",
                    89153334567L, List.of(1L)),
            new NewEmployeeDto("Валентин", "Александрович", "Плотников",
                    89347778294L, List.of(1L, 5L, 6L, 7L)),
            new NewEmployeeDto("Петр", "Иванович", "Абраменко",
                    89183454829L, List.of(1L, 2L, 3L, 4L)),
            new NewEmployeeDto("Григорий", "Олегович", "Костромин",
                    89123345993L, List.of(4L, 5L, 6L, 7L)),
            new NewEmployeeDto("Егор", "Антонович", "Карпов",
                    89155675993L, List.of(1L, 2L, 5L, 6L, 7L)),
            new NewEmployeeDto("Антон", "Петрович", "Рыбин",
                    89132245911L, List.of(1L, 2L, 3L, 4L, 5L, 6L, 7L)),
            new NewEmployeeDto("Аркадий", "Олегович", "Павлов",
                    89113335798L, List.of(1L, 2L, 3L, 4L, 5L)),
            new NewEmployeeDto("Степан", "Степанович", "Никитин",
                    89223245913L, List.of(1L, 2L, 5L, 6L, 7L, 8L, 9L))
    );

    list.stream()
            .filter(e -> !employeeService.existsByCredentials(e.firstName(),
                    e.middleName(),
                    e.lastName()))
            .forEach(employeeService::saveNewEmployee);
  }

  /**
   * Inserting manager data.
   */
  public void insertManager() {
    ManagerRegisterDto dto = new ManagerRegisterDto("Михаил", "Михаилович",
            "Мишин", 89991112233L,
            "man", "1234");

    if (!managerService.existsManagerByUsername(dto.username())) {
      managerService.saveNewManager(dto);
    }
  }

  /**
   * Inserting project data.
   */
  public void insertProject() {
    if (projectService.getAllProjects().size() == 0) {
      NewProjectDto dto1 = new NewProjectDto(
              345,
              "Шкаф",
              40,
              1L,
              "Шишкина М.М.");

      NewProjectDto dto2 = new NewProjectDto(
              346,
              "Дверь",
              25,
              1L,
              "ГосСтройБыт");

      NewProjectDto dto3 = new NewProjectDto(
              284,
              "Стол",
              35,
              1L,
              "Петров В.Г.");

      projectService.saveNewProject(dto1);
      projectService.saveNewProject(dto2);
      projectService.saveNewProject(dto3);
    }
  }

  /**
   * Insertion of data operations.
   */
  public void insertOperations() {
    Project p1 = projectService.getProjectById(1);
    Project p2 = projectService.getProjectById(2);
    Project p3 = projectService.getProjectById(3);

    if (p1.getOperations().isEmpty()) {
      List<NewOperationDto> operations = new ArrayList<>();
      operations.add(new NewOperationDto("Раскрой",
              1, 10));
      operations.add(new NewOperationDto("Кромка",
              2, 20));
      operations.add(new NewOperationDto("Фрезеровка",
              4, 30));
      operations.add(new NewOperationDto("Присадка",
              3, 40));
      operations.add(new NewOperationDto("Сборка",
              6, 50));
      operations.add(new NewOperationDto("Покраска",
              7, 60));
      operations.add(new NewOperationDto("Отгрузка",
              9, 70));
      WrapperNewOperationDto wrapper = new WrapperNewOperationDto(p1.getId(), operations);
      operationService.saveNewOperations(wrapper);
    }

    if (p2.getOperations().isEmpty()) {
      List<NewOperationDto> operations = new ArrayList<>();
      operations.add(new NewOperationDto("Раскрой", 1, 10));
      operations.add(new NewOperationDto("Кромка", 2, 20));
      operations.add(new NewOperationDto("Фрезеровка", 4, 30));
      operations.add(new NewOperationDto("Сборка", 6, 40));
      operations.add(new NewOperationDto("Особый вид покраски - лакировка",
              7, 50));
      operations.add(new NewOperationDto("Отгрузка", 9, 60));
      WrapperNewOperationDto wrapper = new WrapperNewOperationDto(p2.getId(), operations);
      operationService.saveNewOperations(wrapper);
    }

    if (p3.getOperations().isEmpty()) {
      List<NewOperationDto> operations = new ArrayList<>();
      operations.add(new NewOperationDto("Раскрой", 1, 10));
      operations.add(new NewOperationDto("Кромка", 2, 20));
      operations.add(new NewOperationDto("Фрезеровка", 4, 30));
      operations.add(new NewOperationDto("Сборка", 6, 40));
      operations.add(new NewOperationDto("Покраска", 7, 50));
      operations.add(new NewOperationDto("Покраска", 7, 60));
      operations.add(new NewOperationDto("Отгрузка", 9, 70));
      WrapperNewOperationDto wrapper = new WrapperNewOperationDto(p3.getId(), operations);
      operationService.saveNewOperations(wrapper);
    }
  }
}
