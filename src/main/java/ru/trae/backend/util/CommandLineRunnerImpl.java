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
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import ru.trae.backend.dto.employee.EmployeeRegisterDtoReq;
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
@Slf4j
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
    insertEmployeeServiceAccount();
    insertProject();
  }
  
  /**
   * Inserting data types of work.
   */
  public void insertTypeWork() {
    List<NewTypeWorkDto> list = List.of(
        new NewTypeWorkDto("Отгрузка"),
        new NewTypeWorkDto("Раскрой"),
        new NewTypeWorkDto("Кромка"),
        new NewTypeWorkDto("Присадка"),
        new NewTypeWorkDto("Фрезеровка"),
        new NewTypeWorkDto("Склейка"),
        new NewTypeWorkDto("Сборка"),
        new NewTypeWorkDto("Подготовка к покраске"),
        new NewTypeWorkDto("Шлифовка/покраска"),
        new NewTypeWorkDto("Упаковка"));
    
    list.stream()
        .filter(t -> !typeWorkService.existsTypeByName(t.name()))
        .forEach(typeWorkService::saveNewTypeWork);
  }
  
  /**
   * Inserting employee data.
   */
  public void insertEmployees() {
    List<EmployeeRegisterDtoReq> list = List.of(
        new EmployeeRegisterDtoReq("Иван", "Петрович", "Шилов",
            "+7 (918) 333 1212", LocalDate.parse("2021-03-22"),
            List.of(1L, 2L, 3L, 4L, 5L, 6L, 7L)),
        new EmployeeRegisterDtoReq("Николай", "Игоревич", "Иванов",
            "+7 (928) 333 2121", LocalDate.parse("2004-07-17"),
            List.of(3L, 4L, 5L, 6L, 7L)),
        new EmployeeRegisterDtoReq("Владимир", "Васильевич", "Петров",
            "+7 (917) 444 5632", LocalDate.parse("2009-12-10"),
            List.of(8L, 9L)),
        new EmployeeRegisterDtoReq("Александр", "Григорьевич",
            "Красильников", "+7 (927) 123 8899",
            LocalDate.parse("2021-04-10"), List.of(8L, 9L)),
        new EmployeeRegisterDtoReq("Никита", "Владимирович", "Бондаренко",
            "+7 (915) 333 4567", LocalDate.parse("2002-02-13"),
            List.of(1L)),
        new EmployeeRegisterDtoReq("Валентин", "Александрович", "Плотников",
            "+7 (934) 777 8294", LocalDate.parse("2007-07-10"),
            List.of(1L, 5L, 6L, 7L)),
        new EmployeeRegisterDtoReq("Петр", "Иванович", "Абраменко",
            "+7 (918) 345 4829", LocalDate.parse("2020-01-10"),
            List.of(1L, 2L, 3L, 4L)),
        new EmployeeRegisterDtoReq("Григорий", "Олегович", "Костромин",
            "+7 (912) 334 5993", LocalDate.parse("2010-11-11"),
            List.of(4L, 5L, 6L, 7L)),
        new EmployeeRegisterDtoReq("Егор", "Антонович", "Карпов",
            "+7 (915) 567 5993", LocalDate.parse("2021-06-18"),
            List.of(1L, 2L, 5L, 6L, 7L)),
        new EmployeeRegisterDtoReq("Антон", "Петрович", "Рыбин",
            "+7 (913) 224 5911", LocalDate.parse("2005-08-15"),
            List.of(1L, 2L, 3L, 4L, 5L, 6L, 7L)),
        new EmployeeRegisterDtoReq("Аркадий", "Олегович", "Павлов",
            "+7 (911) 333 5798", LocalDate.parse("2008-02-11"),
            List.of(1L, 2L, 3L, 4L, 5L)),
        new EmployeeRegisterDtoReq("Степан", "Степанович", "Никитин",
            "+7 (922) 324 5913", LocalDate.parse("2022-08-10"),
            List.of(1L, 2L, 5L, 6L, 7L, 8L, 9L))
    );
    
    list.stream()
        .filter(e -> !employeeService.existsByCredentials(e.firstName(),
            e.middleName(),
            e.lastName()))
        .forEach(employeeService::saveNewEmployee);
  }
  
  /**
   * Inserting admin data.
   */
  public void insertAdmin() {
    if (managerRepository.existsByUsernameIgnoreCase("admin")) {
      return;
    }
    
    String encodedPass = encoder.encode("TopSec");
    
    Manager m = new Manager();
    m.setFirstName("admin");
    m.setMiddleName("admin");
    m.setLastName("admin");
    m.setPhone("+0 (000) 000 0000");
    m.setUsername("admin");
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
    if (managerRepository.existsByUsernameIgnoreCase("manager8")) {
      return;
    }
    
    ManagerRegisterDto dto = new ManagerRegisterDto("Михаил", "Михаилович",
        "Мишин", "+7 (999) 111 2233",
        "manager8", LocalDate.parse("2022-01-10"));
    
    if (!managerService.existsManagerByUsername(dto.username())) {
      log.info("=================================");
      log.info(managerService.saveNewManager(dto).toString());
      log.info("=================================");
    }
  }
  
  /**
   * Inserting Service account data.
   */
  public void insertEmployeeServiceAccount() {
    if (managerRepository.existsByUsernameIgnoreCase("service_account")) {
      return;
    }
    
    String encodedPass = encoder.encode("Work24x7");
    
    Manager m = new Manager();
    m.setFirstName("service account");
    m.setMiddleName("for");
    m.setLastName("employee");
    m.setPhone("+0 (000) 000 0000");
    m.setUsername("service_account");
    m.setPassword(encodedPass);
    m.setRole(Role.ROLE_EMPLOYEE);
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
   * Inserting project data.
   */
  public void insertProject() {
    if (projectService.getAllProjects().isEmpty()) {
      NewProjectDto dto1 = new NewProjectDto(
          345,
          "Шкаф",
          LocalDateTime.now().plusDays(40),
          "Шишкина М.М.",
          "Комментарий",
          List.of(
              new NewOperationDto("Раскрой", 2),
              new NewOperationDto("Кромка", 3),
              new NewOperationDto("Фрезеровка", 5),
              new NewOperationDto("Присадка", 4),
              new NewOperationDto("Сборка", 7),
              new NewOperationDto("Покраска", 9)));
      
      NewProjectDto dto2 = new NewProjectDto(
          346,
          "Дверь",
          LocalDateTime.now().plusDays(25),
          "ГосСтройБыт",
          "Комментарий",
          List.of(
              new NewOperationDto("Раскрой", 2),
              new NewOperationDto("Кромка", 3),
              new NewOperationDto("Фрезеровка", 5),
              new NewOperationDto("Сборка", 7),
              new NewOperationDto("Лакировка", 9)));
      
      NewProjectDto dto3 = new NewProjectDto(
          284,
          "Стол",
          LocalDateTime.now().plusDays(33),
          "Петров В.Г.",
          null,
          List.of(
              new NewOperationDto("Раскрой", 2),
              new NewOperationDto("Кромка", 3),
              new NewOperationDto("Фрезеровка", 5),
              new NewOperationDto("Сборка", 7),
              new NewOperationDto("Покраска", 9),
              new NewOperationDto("Покраска", 9)));
      
      projectService.saveNewProject(dto1, "manager8");
      projectService.saveNewProject(dto2, "manager8");
      projectService.saveNewProject(dto3, "manager8");
    }
  }
}
