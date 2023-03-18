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
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.trae.backend.dto.employee.EmployeeDto;
import ru.trae.backend.dto.employee.NewEmployeeDto;
import ru.trae.backend.dto.employee.ShortEmployeeDto;
import ru.trae.backend.service.EmployeeService;

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
  public ResponseEntity<ShortEmployeeDto> employeeCheckOut(@PathVariable long employeeId) {
    return ResponseEntity.ok(employeeService.departureEmployee(employeeId));
  }

  /**
   * Endpoint for getting a list of all employees.
   *
   * @return a list of all employees
   */
  @Operation(summary = "Список сотрудников",
      description = "Доступен адмнистратору. Возвращает список ДТО сотрудников")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Список ДТО сотрудников",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = List.class))}),
      @ApiResponse(responseCode = "401", description = "Требуется аутентификация",
          content = @Content),
      @ApiResponse(responseCode = "403", description = "Доступ запрещен", content = @Content),
      @ApiResponse(responseCode = "423", description = "Учетная запись заблокирована",
          content = @Content)})
  @GetMapping("/employees")
  public ResponseEntity<List<EmployeeDto>> employees() {
    return ResponseEntity.ok(employeeService.getAllEmployees());
  }

  /**
   * Endpoint for registering a new employee.
   *
   * @param dto The employee data transfer object
   * @return An HTTP response with a status of 'CREATED'
   * @throws ConstraintViolationException If the credentials provided are already in use
   */
  @Operation(summary = "Регистрация нового сотрудника",
      description = "Доступен администратору. Ничего не возвращает, только статус")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description =
          "Успешное создание учетной записи сотрудника", content = @Content),
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
  public ResponseEntity<HttpStatus> register(@Valid @RequestBody NewEmployeeDto dto) {
    employeeService.checkAvailableCredentials(dto.firstName(), dto.middleName(), dto.lastName());
    employeeService.saveNewEmployee(dto);

    return new ResponseEntity<>(HttpStatus.CREATED);
  }
}
