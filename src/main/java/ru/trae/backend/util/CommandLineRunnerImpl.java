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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import ru.trae.backend.dto.employee.NewEmployeeDto;
import ru.trae.backend.dto.manager.ManagerRegisterDto;
import ru.trae.backend.dto.operation.NewOperationDto;
import ru.trae.backend.dto.project.NewProjectDto;
import ru.trae.backend.dto.type.NewTypeWorkDto;
import ru.trae.backend.entity.user.Manager;
import ru.trae.backend.repository.ManagerRepository;
import ru.trae.backend.service.EmployeeService;
import ru.trae.backend.service.ManagerService;
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
  private final ManagerRepository managerRepository;
  private final BCryptPasswordEncoder encoder;

  @Override
  public void run(String... args) {
    insertTypeWork();
    insertEmployees();
    insertAdmin();
    insertManager();
    insertProject();
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
            "+7 (918) 333 1212", LocalDate.parse("2021-03-22"),
            List.of(1L, 2L, 3L, 4L, 5L, 6L, 7L)),
        new NewEmployeeDto("Николай", "Игоревич", "Иванов",
            "+7 (928) 333 2121", LocalDate.parse("2004-07-17"),
            List.of(3L, 4L, 5L, 6L, 7L)),
        new NewEmployeeDto("Владимир", "Васильевич", "Петров",
            "+7 (917) 444 5632", LocalDate.parse("2009-12-10"),
            List.of(8L, 9L)),
        new NewEmployeeDto("Александр", "Григорьевич",
            "Красильников", "+7 (927) 123 8899",
            LocalDate.parse("2021-04-10"), List.of(8L, 9L)),
        new NewEmployeeDto("Никита", "Владимирович", "Бондаренко",
            "+7 (915) 333 4567", LocalDate.parse("2002-02-13"),
            List.of(1L)),
        new NewEmployeeDto("Валентин", "Александрович", "Плотников",
            "+7 (934) 777 8294", LocalDate.parse("2007-07-10"),
            List.of(1L, 5L, 6L, 7L)),
        new NewEmployeeDto("Петр", "Иванович", "Абраменко",
            "+7 (918) 345 4829", LocalDate.parse("2020-01-10"),
            List.of(1L, 2L, 3L, 4L)),
        new NewEmployeeDto("Григорий", "Олегович", "Костромин",
            "+7 (912) 334 5993", LocalDate.parse("2010-11-11"),
            List.of(4L, 5L, 6L, 7L)),
        new NewEmployeeDto("Егор", "Антонович", "Карпов",
            "+7 (915) 567 5993", LocalDate.parse("2021-06-18"),
            List.of(1L, 2L, 5L, 6L, 7L)),
        new NewEmployeeDto("Антон", "Петрович", "Рыбин",
            "+7 (913) 224 5911", LocalDate.parse("2005-08-15"),
            List.of(1L, 2L, 3L, 4L, 5L, 6L, 7L)),
        new NewEmployeeDto("Аркадий", "Олегович", "Павлов",
            "+7 (911) 333 5798", LocalDate.parse("2008-02-11"),
            List.of(1L, 2L, 3L, 4L, 5L)),
        new NewEmployeeDto("Степан", "Степанович", "Никитин",
            "+7 (922) 324 5913", LocalDate.parse("2022-08-10"),
            List.of(1L, 2L, 5L, 6L, 7L, 8L, 9L))
    );

    list.stream()
        .filter(e -> !employeeService.existsByCredentials(e.firstName(),
            e.middleName(),
            e.lastName()))
        .forEach(employeeService::saveNewEmployee);
  }

  public void insertAdmin() {
    if (managerRepository.existsByUsernameIgnoreCase("Admin")) {
      return;
    }

    String encodedPass = encoder.encode("TopSec");

    Manager m = new Manager();
    m.setFirstName("Admin");
    m.setMiddleName("Admin");
    m.setLastName("Admin");
    m.setPhone("+0 (000) 000 0000");
    m.setUsername("Admin");
    m.setPassword(encodedPass);
    m.setRole(Role.ROLE_ADMINISTRATOR);
    m.setDateOfRegister(LocalDate.now());
    m.setDateOfEmployment(LocalDate.now());
    m.setDateOfDismissal(null);

    m.setEnabled(true);
    m.setAccountNonExpired(true);
    m.setAccountNonLocked(true);
    m.setCredentialsNonExpired(true);

    managerRepository.save(m);
  }

  /**
   * Inserting manager data.
   */
  public void insertManager() {
    if (managerRepository.existsByUsernameIgnoreCase("Manager8")) {
      return;
    }

    ManagerRegisterDto dto = new ManagerRegisterDto("Михаил", "Михаилович",
        "Мишин", "+7 (999) 111 2233",
        "Manager8", LocalDate.parse("2022-01-10"));

    if (!managerService.existsManagerByUsername(dto.username())) {
      System.out.println("=================================");
      System.out.println(managerService.saveNewManager(dto));
      System.out.println("=================================");
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
          LocalDateTime.parse("2023-05-10T11:22:33"),
          "Шишкина М.М.",
          "Комментарий",
          List.of(
              new NewOperationDto("Раскрой", 1),
              new NewOperationDto("Кромка", 2),
              new NewOperationDto("Фрезеровка", 4),
              new NewOperationDto("Присадка", 3),
              new NewOperationDto("Сборка", 6),
              new NewOperationDto("Покраска", 7)));

      NewProjectDto dto2 = new NewProjectDto(
          346,
          "Дверь",
          LocalDateTime.parse("2023-04-15T18:21:33"),
          "ГосСтройБыт",
          "Комментарий",
          List.of(
              new NewOperationDto("Раскрой", 1),
              new NewOperationDto("Кромка", 2),
              new NewOperationDto("Фрезеровка", 4),
              new NewOperationDto("Сборка", 6),
              new NewOperationDto("Лакировка", 7)));

      NewProjectDto dto3 = new NewProjectDto(
          284,
          "Стол",
          LocalDateTime.parse("2023-04-18T08:23:33"),
          "Петров В.Г.",
          null,
          List.of(
              new NewOperationDto("Раскрой", 1),
              new NewOperationDto("Кромка", 2),
              new NewOperationDto("Фрезеровка", 4),
              new NewOperationDto("Сборка", 6),
              new NewOperationDto("Покраска", 7),
              new NewOperationDto("Покраска", 7)));

      projectService.saveNewProject(dto1, "Manager8");
      projectService.saveNewProject(dto2, "Manager8");
      projectService.saveNewProject(dto3, "Manager8");
    }
  }
}
