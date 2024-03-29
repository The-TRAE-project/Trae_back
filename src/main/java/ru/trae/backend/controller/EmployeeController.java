/*
 * Copyright (c) 2023. Vladimir Olennikov.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.trae.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.List;
import java.util.Set;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.trae.backend.dto.PageDto;
import ru.trae.backend.dto.employee.ChangeDataDtoReq;
import ru.trae.backend.dto.employee.EmployeeDto;
import ru.trae.backend.dto.employee.EmployeeIdFirstLastNameDto;
import ru.trae.backend.dto.employee.EmployeeRegisterDtoReq;
import ru.trae.backend.dto.employee.EmployeeRegisterDtoResp;
import ru.trae.backend.dto.employee.ShortEmployeeDto;
import ru.trae.backend.service.EmployeeService;
import ru.trae.backend.util.PageSettings;

/**
 * Controller for handling employee related requests.
 *
 * @author Vladimir Olennikov
 */
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/employee")
public class EmployeeController {
  private final EmployeeService employeeService;
  
  /**
   * Endpoint for checking in an employee with a given pin.
   *
   * @param pin the employee's pin code
   * @return the employee's information
   */
  @Operation(summary = "Логин",
      description = "Доступен сотрудникам. Возвращает укороченное ДТО сотрудника")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Укороченное ДТО сотрудника",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = ShortEmployeeDto.class))}),
      @ApiResponse(responseCode = "400", description =
          "Неправильный формат пин кода, выход за пределы диапазона 100-999", content = @Content),
      @ApiResponse(responseCode = "401", description = "Требуется аутентификация",
          content = @Content),
      @ApiResponse(responseCode = "403", description = "Доступ запрещен", content = @Content),
      @ApiResponse(responseCode = "404",
          description = "Сотрудник с таким пин кодом не найден", content = @Content),
      @ApiResponse(responseCode = "423", description = "Учетная запись заблокирована",
          content = @Content)})
  @GetMapping("/login/{pin}")
  public ResponseEntity<ShortEmployeeDto> employeeLogin(
      @PathVariable @Parameter(description = "Пин код сотрудника")
      @Min(value = 100, message = "The pin code cannot be less than 100")
      @Max(value = 999, message = "The pin code cannot be more than 999") int pin) {
    return ResponseEntity.ok(employeeService.employeeLogin(pin));
  }
  
  /**
   * Endpoint for confirming the arrival of an employee for a shift.
   *
   * @param employeeId the employee's ID
   * @return the employee's information
   */
  @Operation(summary = "Отметка о прибытии на смену",
      description = "Доступен сотрудникам. Возвращает укороченное ДТО сотрудника")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Укороченное ДТО сотрудника",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = ShortEmployeeDto.class))}),
      @ApiResponse(responseCode = "400", description =
          "Неправильный формат идентификатора сотрудника", content = @Content),
      @ApiResponse(responseCode = "401", description = "Требуется аутентификация",
          content = @Content),
      @ApiResponse(responseCode = "403", description = "Доступ запрещен", content = @Content),
      @ApiResponse(responseCode = "404",
          description = "Сотрудник с таким идентификатором не найден", content = @Content),
      @ApiResponse(responseCode = "423", description = "Учетная запись заблокирована",
          content = @Content)})
  @PostMapping("/checkin/{employeeId}")
  public ResponseEntity<ShortEmployeeDto> employeeCheckIn(
      @PathVariable @Parameter(description = "Идентификатор сотрудника") long employeeId) {
    return ResponseEntity.ok(employeeService.checkInEmployee(employeeId));
  }
  
  /**
   * Endpoint for checking out an employee with a given id.
   *
   * @param employeeId the employee's id
   * @return the employee's information
   */
  @Operation(summary = "Отметка об убытии со смены",
      description = "Доступен сотрудникам. Возвращает укороченное ДТО сотрудника")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Укороченное ДТО сотрудника",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = ShortEmployeeDto.class))}),
      @ApiResponse(responseCode = "400", description =
          "Неправильный формат идентификатора сотрудника", content = @Content),
      @ApiResponse(responseCode = "401", description = "Требуется аутентификация",
          content = @Content),
      @ApiResponse(responseCode = "403", description = "Доступ запрещен", content = @Content),
      @ApiResponse(responseCode = "404",
          description = "Сотрудник с таким идентификатором не найден", content = @Content),
      @ApiResponse(responseCode = "423", description = "Учетная запись заблокирована",
          content = @Content)})
  @PostMapping("/checkout/{employeeId}")
  public ResponseEntity<ShortEmployeeDto> employeeCheckOut(
      @PathVariable @Parameter(description = "Идентификатор сотрудника") long employeeId) {
    return ResponseEntity.ok(employeeService.departureEmployee(employeeId));
  }
  
  /**
   * Retrieves a paginated list of employees based on the specified parameters.
   *
   * @param pageSetting The page settings for pagination (e.g., page number, elements per page).
   * @param typeWorkId  Optional. A list of role IDs used for filtering employees by role(s).
   * @param isActive    Optional. A flag indicating whether to filter employees by active status.
   * @return A ResponseEntity containing a PageDto of EmployeeDto object representing the paginated
   *     list of employees.
   */
  @Operation(summary = "Список ДТО сотрудников с пагинацией",
      description = "Доступен администратору. Возвращает список ДТО сотрудников. "
          + "В примере указан единичный объект из списка")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Список ДТО сотрудников",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = EmployeeDto.class))}),
      @ApiResponse(responseCode = "401", description = "Требуется аутентификация",
          content = @Content),
      @ApiResponse(responseCode = "403", description = "Доступ запрещен", content = @Content),
      @ApiResponse(responseCode = "423", description = "Учетная запись заблокирована",
          content = @Content)})
  @GetMapping("/employees")
  public ResponseEntity<PageDto<EmployeeDto>> employeesWithPagination(
      @Valid PageSettings pageSetting,
      @RequestParam(required = false) @Parameter(description = "Фильтрация по роли(ям)")
      List<Long> typeWorkId,
      @RequestParam(required = false) @Parameter(description = "Фильтрация по статусу")
      Boolean isActive) {
    
    Sort employeeSort = pageSetting.buildManagerOrEmpSort();
    Pageable employeePage = PageRequest.of(
        pageSetting.getPage(), pageSetting.getElementPerPage(), employeeSort);
    return ResponseEntity.ok(
        employeeService.getEmployeeDtoPage(employeePage, typeWorkId, isActive));
  }
  
  /**
   * Endpoint for getting a list of all employees without pagination.
   *
   * @return a list of all employees
   */
  @Operation(summary = "Список сокращенных ДТО сотрудников без пагинации с фильтрами по проектам "
      + "и операциям",
      description = "Доступен администратору. Возвращает список сокращенных ДТО (id, имя, фамилия) "
          + "сотрудников с возможной фильтрацией по идентификаторами проектов или операций. "
          + "В примере указан единичный объект из списка")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Список сокращенных ДТО сотрудников",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = EmployeeIdFirstLastNameDto.class))}),
      @ApiResponse(responseCode = "401", description = "Требуется аутентификация",
          content = @Content),
      @ApiResponse(responseCode = "403", description = "Доступ запрещен", content = @Content),
      @ApiResponse(responseCode = "423", description = "Учетная запись заблокирована",
          content = @Content)})
  @GetMapping("/employees/list")
  public ResponseEntity<List<EmployeeIdFirstLastNameDto>> employeesForReportWithoutPagination(
      @RequestParam(required = false) @Parameter(description = "Фильтр сотрудников по "
          + "идентификаторам проектов в которых они участвовали") Set<Long> projectIds,
      @RequestParam(required = false) @Parameter(description = "Фильтр сотрудников по "
          + "идентификаторам операций в которых они участвовали") Set<Long> operationIds) {
    return ResponseEntity.ok(
        employeeService.getEmployeeIdFirstLastNameDtoList(projectIds, operationIds));
  }
  
  /**
   * Endpoint for registering a new employee.
   *
   * @param dto the new employee details
   * @return the {@link ResponseEntity} of {@link EmployeeRegisterDtoResp} with HTTP status 201
   *     (Created)
   * @throws ConstraintViolationException If the credentials provided are already in use
   */
  @Operation(summary = "Регистрация нового сотрудника",
      description = "Доступен администратору. Возвращает имя, фамилию и пинкод сотрудника")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201",
          description = "Успешное создание учетной записи сотрудника",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = EmployeeRegisterDtoResp.class))}),
      @ApiResponse(responseCode = "400", description =
          "Неправильный формат данных создаваемого сотрудника", content = @Content),
      @ApiResponse(responseCode = "401", description = "Требуется аутентификация",
          content = @Content),
      @ApiResponse(responseCode = "403", description = "Доступ запрещен", content = @Content),
      @ApiResponse(responseCode = "409",
          description = "Сотрудник с таким данными (ФИО) уже существует", content = @Content),
      @ApiResponse(responseCode = "423", description = "Учетная запись заблокирована",
          content = @Content)})
  @PostMapping("/register")
  public ResponseEntity<EmployeeRegisterDtoResp> register(
      @Valid @RequestBody EmployeeRegisterDtoReq dto) {
    employeeService.checkAvailableCredentials(dto.firstName(), dto.middleName(), dto.lastName());
    
    return new ResponseEntity<>(employeeService.saveNewEmployee(dto), HttpStatus.CREATED);
  }
  
  /**
   * Endpoint for change employee data and work type.
   *
   * @param dto - Data transfer object for change employee data
   * @return - data transfer object with changed employee data
   */
  @Operation(summary = "Изменение учетных данных, типов работ, пин кода сотрудника, "
      + "отключение/включение учетной записи",
      description = "Доступен администратору. Изменяет учетные данные, пин код, статус, типы работ"
          + " выбранного сотрудника")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Возвращает ДТО с учетными данными, типами"
          + " работ, статусом, датой приема на работу, датой увольнения (если ее нет, то NULL)",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = EmployeeDto.class))}),
      @ApiResponse(responseCode = "400", description = "Неправильный формат учетных данных,"
          + " пин кода, статуса или даты увольнения/приема на работу",
          content = @Content),
      @ApiResponse(responseCode = "401", description = "Требуется аутентификация",
          content = @Content),
      @ApiResponse(responseCode = "403", description = "Доступ запрещен",
          content = @Content),
      @ApiResponse(responseCode = "404",
          description = "Сотрудник с таким идентификатором не найден", content = @Content),
      @ApiResponse(responseCode = "409", description = "Сотрудник уже имеет такие учетные"
          + " данные, типы работ, учетная запись уже отключена/включена",
          content = @Content),
      @ApiResponse(responseCode = "423", description = "Учетная запись заблокирована",
          content = @Content)})
  @PostMapping("/change-data")
  public ResponseEntity<EmployeeDto> changeData(@Valid @RequestBody ChangeDataDtoReq dto) {
    employeeService.changeEmployeeDataAndStatusAndPinCodeAndTypesWork(dto);
    
    return ResponseEntity.ok(employeeService.getEmpDtoById(dto.employeeId()));
  }
}
