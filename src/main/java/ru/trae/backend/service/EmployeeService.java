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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.trae.backend.dto.PageDto;
import ru.trae.backend.dto.employee.ChangeDataDtoReq;
import ru.trae.backend.dto.employee.EmployeeDto;
import ru.trae.backend.dto.employee.EmployeeRegisterDtoReq;
import ru.trae.backend.dto.employee.EmployeeRegisterDtoResp;
import ru.trae.backend.dto.employee.ShortEmployeeDto;
import ru.trae.backend.dto.mapper.EmployeeDtoMapper;
import ru.trae.backend.dto.mapper.PageToPageDtoMapper;
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
  private final PageToPageDtoMapper pageToPageDtoMapper;

  /**
   * Method for saving new employee to the database.
   *
   * @param dto contains data for creating a new employee
   * @return a saved employee entity
   */
  public EmployeeRegisterDtoResp saveNewEmployee(EmployeeRegisterDtoReq dto) {
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
    e.setDateOfRegister(LocalDate.now());
    e.setDateOfEmployment(dto.dateOfEmployment());
    e.setDateOfDismissal(null);

    Employee savedEmp = employeeRepository.save(e);

    return new EmployeeRegisterDtoResp(
        savedEmp.getFirstName(), savedEmp.getLastName(), savedEmp.getPinCode());
  }

  /**
   * Retrieves a specific employee by id.
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

  /**
   * Method for checking in an employee with a given pin.
   *
   * @param pinCode the employee's pin code
   * @return the shortened dto of the employee
   */
  public ShortEmployeeDto employeeLogin(int pinCode) {
    Optional<Employee> e = employeeRepository.findByPinCode(pinCode);

    if (e.isEmpty()) {
      throw new EmployeeException(HttpStatus.NOT_FOUND,
          "Employee with pin code: " + pinCode + " not found");
    }

    if (!e.get().isActive()) {
      throw new EmployeeException(HttpStatus.LOCKED, "The account is disabled");
    }

    return new ShortEmployeeDto(
        e.get().getId(),
        e.get().getFirstName(),
        e.get().getLastName(),
        workingShiftService.employeeOnShift(true, e.get().getId()));
  }

  /**
   * A method for confirming the arrival of an employee for a shift.
   *
   * @param employeeId employee id
   * @return the shortened dto of the employee
   */
  public ShortEmployeeDto checkInEmployee(long employeeId) {
    Employee e = getEmployeeById(employeeId);

    if (!workingShiftService.employeeOnShift(true, e.getId())) {
      workingShiftService.arrivalEmployeeOnShift(e);
    }

    return new ShortEmployeeDto(
        e.getId(),
        e.getFirstName(),
        e.getLastName(),
        workingShiftService.employeeOnShift(true, e.getId()));
  }

  /**
   * Method of confirming the employee's departure from the work shift.
   * Assigns the time of the employee's departure in the active work shift.
   *
   * @param employeeId employee id number
   * @return the shortened dto of the employee
   */
  public ShortEmployeeDto departureEmployee(long employeeId) {
    Employee e = getEmployeeById(employeeId);

    if (workingShiftService.employeeOnShift(true, e.getId())) {
      timeControlService.updateTimeControlForDeparture(employeeId, LocalDateTime.now());
    }

    return new ShortEmployeeDto(
        e.getId(),
        e.getFirstName(),
        e.getLastName(),
        workingShiftService.employeeOnShift(true, e.getId()));
  }

  /**
   * Get Employee Page.
   *
   * @param employeePage page
   * @param typeWorkId   type work id
   * @param isActive     is active
   * @return page of employees
   */
  public Page<Employee> getEmployeePage(Pageable employeePage, Long typeWorkId, Boolean isActive) {
    Page<Employee> page;

    if (isActive != null && typeWorkId != null) {
      page = employeeRepository.findByIsActiveAndTypeWorks_Id(isActive, typeWorkId, employeePage);
    } else if (isActive != null) {
      page = employeeRepository.findByIsActive(isActive, employeePage);
    } else if (typeWorkId != null) {
      page = employeeRepository.findByTypeWorksId(typeWorkId, employeePage);
    } else {
      page = employeeRepository.findAll(employeePage);
    }
    return page;
  }

  public PageDto<EmployeeDto> getEmployeeDtoPage(
      Pageable employeePage, Long typeWorkId, Boolean isActive) {
    return pageToPageDtoMapper.employeePageToPageDto(
        getEmployeePage(employeePage, typeWorkId, isActive));
  }

  /**
   * Checks if an employee exists based on their pin code.
   *
   * @param pinCode the PIN code of the employee to check
   * @return true if an employee exists with the specified pin code, false otherwise
   */
  public boolean existsEmpByPinCode(int pinCode) {
    return employeeRepository.existsByPinCode(pinCode);
  }

  /**
   * Checks if an employee exists in the database with the given first name, middle name, and last
   * name.
   *
   * @param firstName  the first name of the employee
   * @param middleName the middle name of the employee
   * @param lastName   the last name of the employee
   * @return true if the employee exists, false if the employee does not exist
   */
  public boolean existsByCredentials(String firstName, String middleName, String lastName) {
    return employeeRepository.existsByFirstMiddleLastNameIgnoreCase(
        firstName,
        middleName,
        lastName);
  }

  /**
   * Checks whether employee with specified credentials already exists.
   *
   * @param firstName  employee's first name
   * @param middleName employee's middle name
   * @param lastName   employee's last name
   * @throws EmployeeException if such employee already exists
   */
  public void checkAvailableCredentials(String firstName, String middleName, String lastName) {
    if (existsByCredentials(firstName, middleName, lastName)) {
      throw new EmployeeException(HttpStatus.CONFLICT, "Such credentials are already in use");
    }
  }

  /**
   * Change employee data, status, pin code and types work.
   *
   * @param dto the change data dto request
   */
  @Transactional
  public void changeEmployeeDataAndStatusAndPinCodeAndTypesWork(ChangeDataDtoReq dto) {
    Employee e = getEmployeeById(dto.employeeId());

    changeEmployeeData(dto, e);
    changeEmployeeStatus(dto, e);
    changePinCode(dto, e);
    changeDateOfEmployment(dto, e);
    changeEmployeeTypesWork(dto, e);

    employeeRepository.save(e);
  }

  private void changeDateOfEmployment(ChangeDataDtoReq dto, Employee e) {
    if (dto.dateOfEmployment() != null) {
      e.setDateOfRegister(dto.dateOfEmployment());
    }
  }

  private void changePinCode(ChangeDataDtoReq dto, Employee e) {
    if (dto.pinCode() == null) {
      return;
    }

    if (existsEmpByPinCode(dto.pinCode())) {
      throw new EmployeeException(HttpStatus.CONFLICT, "This pin code already used");
    } else {
      e.setPinCode(dto.pinCode());
    }
  }

  private void changeEmployeeData(ChangeDataDtoReq dto, Employee e) {
    if (dto.firstName() != null) {
      if (dto.firstName().equals(e.getFirstName())) {
        throw new EmployeeException(HttpStatus.CONFLICT,
            "The new first name must not match the current one");
      }
      e.setFirstName(dto.firstName());
    }
    if (dto.middleName() != null) {
      if (dto.middleName().equals(e.getMiddleName())) {
        throw new EmployeeException(HttpStatus.CONFLICT,
            "The new middle name must not match the current one");
      }
      e.setMiddleName(dto.middleName());
    }
    if (dto.lastName() != null) {
      if (dto.lastName().equals(e.getLastName())) {
        throw new EmployeeException(HttpStatus.CONFLICT,
            "The new last name must not match the current one");
      }
      e.setLastName(dto.lastName());
    }
    if (dto.phone() != null) {
      if (dto.phone().equals(e.getPhone())) {
        throw new EmployeeException(HttpStatus.CONFLICT,
            "The new phone must not match the current one");
      }
      e.setPhone(dto.phone());
    }
  }

  private void changeEmployeeStatus(ChangeDataDtoReq dto, Employee e) {
    if (dto.dateOfDismissal() == null && dto.isActive() == null) {
      return;
    }

    if (dto.isActive() != null) {
      if ((e.isActive() != dto.isActive()) && Boolean.TRUE.equals(dto.isActive())) {
        e.setActive(true);
        e.setDateOfDismissal(null);
      } else if ((e.isActive() != dto.isActive()) && (dto.dateOfDismissal() != null)) {
        e.setActive(false);
        e.setDateOfDismissal(dto.dateOfDismissal());
      }
    } else {
      throw new EmployeeException(HttpStatus.BAD_REQUEST,
          "Incorrect status or missing date of dismissal");
    }
  }

  private void changeEmployeeTypesWork(ChangeDataDtoReq dto, Employee e) {
    if (dto.changedTypesId() == null) {
      return;
    }

    final Set<TypeWork> typeWorks = dto.changedTypesId().stream()
        .map(typeWorkService::getTypeWorkById)
        .collect(Collectors.toSet());

    if (typeWorks.containsAll(e.getTypeWorks()) && e.getTypeWorks().containsAll(typeWorks)) {
      throw new EmployeeException(HttpStatus.CONFLICT,
          "The employee already has these types of works");
    }

    e.getTypeWorks().clear();
    e.getTypeWorks().addAll(typeWorks);
  }
}
