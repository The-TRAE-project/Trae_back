/*
 * Copyright (c) 2023. Vladimir Olennikov.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.trae.backend.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.trae.backend.dto.employee.EmployeeDto;
import ru.trae.backend.dto.employee.NewEmployeeDto;
import ru.trae.backend.dto.employee.ShortEmployeeDto;
import ru.trae.backend.dto.mapper.EmployeeDtoMapper;
import ru.trae.backend.entity.TypeWork;
import ru.trae.backend.entity.user.Employee;
import ru.trae.backend.exceptionhandler.exception.EmployeeException;
import ru.trae.backend.repository.EmployeeRepository;
import ru.trae.backend.util.Util;

/**
 * Service class for working with employee data.
 *
 * @author Vladimir Olennikov
 */
@Service
@RequiredArgsConstructor
public class EmployeeService {
  private final EmployeeRepository employeeRepository;
  private final EmployeeDtoMapper employeeDtoMapper;
  private final WorkingShiftService workingShiftService;
  private final TimeControlService timeControlService;
  private final TypeWorkService typeWorkService;

  /**
   * Method for saving new employee to the database.
   *
   * @param dto contains data for creating a new employee
   * @return a saved employee entity
   */
  public Employee saveNewEmployee(NewEmployeeDto dto) {
    int randomPinCode;
    do {
      randomPinCode = Util.generateRandomInteger(100, 999);
    } while (existsEmpByPinCode(randomPinCode));

    final Set<TypeWork> typeWorks = dto.typesId().stream()
            .map(typeWorkService::getTypeWorkById)
            .collect(Collectors.toSet());

    Employee e = new Employee();
    e.setFirstName(dto.firstName());
    e.setMiddleName(dto.middleName());
    e.setLastName(dto.lastName());
    e.setPhone(dto.phone());
    e.setPinCode(randomPinCode);
    e.getTypeWorks().addAll(typeWorks);
    e.setActive(true);
    e.setDateOfRegister(LocalDateTime.now());

    return employeeRepository.save(e);
  }

  /**
   * Retrieves a specific employee by ID.
   *
   * @param id The ID of the desired employee
   * @return The employee with the specified ID
   * @throws EmployeeException When the employee with the specified ID is not found
   */
  public Employee getEmployeeById(long id) {
    return employeeRepository.findById(id).orElseThrow(
            () -> new EmployeeException(HttpStatus.NOT_FOUND,
                    "Employee with ID: " + id + " not found"));
  }

  public EmployeeDto getEmpDtoById(long id) {
    return employeeDtoMapper.apply(getEmployeeById(id));
  }

  public ShortEmployeeDto getShortDtoEmpById(long id) {
    Employee e = getEmployeeById(id);
    return new ShortEmployeeDto(e.getId(), e.getFirstName(), e.getLastName());
  }

  /**
   * A method for confirming the arrival of an employee for a shift.
   * The existence of the pin code in the database and the status of the
   * employee's account are checked.
   *
   * @param pin employee pin code
   * @return the shortened dto of the employee
   */
  public ShortEmployeeDto checkInEmployee(int pin) {
    Optional<Employee> employee = employeeRepository.findByPinCode(pin);

    if (employee.isEmpty()) {
      throw new EmployeeException(HttpStatus.NOT_FOUND,
              "Employee with pin code: " + pin + " not found");
    }

    if (!employee.get().isActive()) {
      throw new EmployeeException(HttpStatus.FORBIDDEN, "The account is disabled");
    }

    if (!workingShiftService.employeeOnShift(true, employee.get().getId())) {
      workingShiftService.arrivalEmployeeOnShift(employee.get());
    }

    return new ShortEmployeeDto(employee.get().getId(),
            employee.get().getFirstName(),
            employee.get().getLastName());
  }

  /**
   * Method of confirming the employee's departure from the work shift.
   * Assigns the time of the employee's departure in the active work shift.
   *
   * @param id employee id number
   * @return the shortened dto of the employee
   */
  public ShortEmployeeDto departureEmployee(long id) {
    Employee e = getEmployeeById(id);

    if (workingShiftService.employeeOnShift(true, e.getId())) {
      timeControlService.updateTimeControlForDeparture(id, LocalDateTime.now());
    }

    return new ShortEmployeeDto(e.getId(), e.getFirstName(), e.getLastName());
  }

  /**
   * Method returns all employees in the repository.
   *
   * @return a list of {@link EmployeeDto} objects
   */
  public List<EmployeeDto> getAllEmployees() {
    return employeeRepository.findAll()
            .stream()
            .map(employeeDtoMapper)
            .toList();
  }

  public boolean existsEmpByPinCode(int pinCode) {
    return employeeRepository.existsByPinCode(pinCode);
  }

  public boolean existsByCredentials(String firstName, String middleName, String lastName) {
    return employeeRepository.existsByFirstMiddleLastNameIgnoreCase(
            firstName,
            middleName,
            lastName);
  }

  /**
   * Checks whether employee with specified credentials already exists.
   *
   * @param firstName employee's first name
   * @param middleName employee's middle name
   * @param lastName employee's last name
   * @throws EmployeeException if such employee already exists
   */
  public void checkAvailableCredentials(String firstName, String middleName, String lastName) {
    if (existsByCredentials(firstName, middleName, lastName)) {
      throw new EmployeeException(HttpStatus.CONFLICT, "Such credentials are already in use");
    }
  }

}
