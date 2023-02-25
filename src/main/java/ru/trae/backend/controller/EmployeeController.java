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

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.trae.backend.dto.employee.EmployeeDto;
import ru.trae.backend.dto.employee.NewEmployeeDto;
import ru.trae.backend.dto.employee.ShortEmployeeDto;
import ru.trae.backend.entity.user.Employee;
import ru.trae.backend.service.EmployeeService;

/**
 * Controller for handling employee related requests.
 *
 * @author Vladimir Olennikov
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/employee")
public class EmployeeController {
  private final EmployeeService employeeService;

  /**
   * Endpoint for checking in an employee with a given pin.
   *
   * @param pin the employee's pin
   * @return the employee's information
   */
  @GetMapping("/checkin/{pin}")
  public ResponseEntity<ShortEmployeeDto> employeeCheckIn(@PathVariable int pin) {
    return ResponseEntity.ok(employeeService.checkInEmployee(pin));
  }

  /**
   * Endpoint for checking out an employee with a given id.
   *
   * @param id the employee's id
   * @return the employee's information
   */
  @GetMapping("/checkout/{id}")
  public ResponseEntity<ShortEmployeeDto> employeeCheckOut(@PathVariable long id) {
    return ResponseEntity.ok(employeeService.departureEmployee(id));
  }

  /**
   * Endpoint for getting a list of all employees.
   *
   * @return a list of all employees
   */
  @GetMapping("/employees")
  public ResponseEntity<List<EmployeeDto>> employees() {
    return ResponseEntity.ok(employeeService.getAllEmployees());
  }

  /**
   * Endpoint for registering a new employee.
   *
   * @param dto the information for the new employee
   * @return the new employee's information
   */
  @PostMapping("/register")
  public ResponseEntity<EmployeeDto> register(@RequestBody NewEmployeeDto dto) {
    employeeService.checkAvailableCredentials(dto.firstName(), dto.middleName(), dto.lastName());
    Employee e = employeeService.saveNewEmployee(dto);
    return ResponseEntity.ok(employeeService.getEmpDtoById(e.getId()));
  }
}
