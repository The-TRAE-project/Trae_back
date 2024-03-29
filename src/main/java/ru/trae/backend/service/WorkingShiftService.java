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
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.trae.backend.dto.mapper.WorkingShiftDtoMapper;
import ru.trae.backend.dto.workingshift.WorkingShiftDto;
import ru.trae.backend.entity.TimeControl;
import ru.trae.backend.entity.WorkingShift;
import ru.trae.backend.entity.user.Employee;
import ru.trae.backend.exceptionhandler.exception.WorkingShiftException;
import ru.trae.backend.projection.WorkingShiftEmployeeDto;
import ru.trae.backend.repository.WorkingShiftRepository;

/**
 * Service class to handle WorkingShift operations.
 *
 * @author Vladimir Olennikov
 */
@Service
@RequiredArgsConstructor
public class WorkingShiftService {
  private final WorkingShiftRepository workingShiftRepository;
  private final TimeControlService timeControlService;
  private final WorkingShiftDtoMapper workingShiftDtoMapper;

  /**
   * Creates a new WorkingShift.
   */
  public void createWorkingShift() {
    WorkingShift ws = new WorkingShift();
    ws.setStartShift(LocalDateTime.now());
    ws.setEnded(false);

    workingShiftRepository.save(ws);
  }

  /**
   * Gets the active WorkingShift.
   *
   * @return the WorkingShiftDto representing the active WorkingShift.
   * @throws WorkingShiftException if there is no active WorkingShift.
   */
  public WorkingShiftDto getActive() {
    if (!existsActiveWorkingShift()) {
      throw new WorkingShiftException(HttpStatus.BAD_REQUEST, "Active work shift not found");
    }

    return workingShiftDtoMapper.apply(workingShiftRepository.findByIsEndedFalse());
  }

  /**
   * Closes the active WorkingShift.
   */
  @Transactional
  public void closeWorkingShift() {
    if (!existsActiveWorkingShift()) {
      return;
    }

    WorkingShift ws = workingShiftRepository.findByIsEndedFalse();
    LocalTime specificTime = LocalTime.of(23, 0);

    ws.getTimeControls().stream()
        .filter(TimeControl::isOnShift)
        .forEach(timeControlService::autoClosingShift);

    ws.setEnded(true);
    ws.setEndShift(ws.getStartShift().with(specificTime));

    workingShiftRepository.save(ws);
  }

  /**
   * Creates a TimeControl for the employee who has arrived.
   *
   * @param employee the employee who has arrived.
   * @throws WorkingShiftException if there is no active WorkingShift.
   */
  public void arrivalEmployeeOnShift(Employee employee) {
    if (!existsActiveWorkingShift()) {
      throw new WorkingShiftException(HttpStatus.BAD_REQUEST, "Active work shift not found");
    }

    WorkingShift ws = workingShiftRepository.findByIsEndedFalse();

    ws.getTimeControls().add(timeControlService.createArrivalTimeControl(employee, ws,
        true, LocalDateTime.now()));
    workingShiftRepository.save(ws);
  }

  /**
   * Checks if there is an active WorkingShift.
   *
   * @return true if there is an active WorkingShift; false otherwise.
   */
  public boolean existsActiveWorkingShift() {
    return workingShiftRepository.existsByIsEndedFalse();
  }

  /**
   * Checks if there exists a working shift that is not ended and has a start shift date
   * different from the current date.
   *
   * @return {@code true} if such a working shift exists, {@code false} otherwise.
   */
  public boolean existsByIsEndedFalseAndStartShiftNotCurrentDate() {
    return workingShiftRepository.existsByIsEndedFalseAndStartShiftNotCurrentDate();
  }

  /**
   * Checks if an employee is on shift.
   *
   * @param isOnShift the status of the employee.
   * @param empId     the id of the employee.
   * @return true if the employee is on shift; false otherwise.
   */
  public boolean employeeOnShift(boolean isOnShift, long empId) {
    return workingShiftRepository.existsEmpOnShift(isOnShift, empId);
  }

  /**
   * Retrieves a list of WorkingShiftEmployeeDto objects for the specified employee IDs and time
   * period.
   *
   * @param startOfPeriod The start date of the time period.
   * @param endOfPeriod   The end date of the time period.
   * @param employeeIds   A set of employee IDs for filtering the results.
   *                      If null or empty, all employees will be considered.
   * @return A list of WorkingShiftEmployeeDto objects representing the working shift details.
   */
  public List<WorkingShiftEmployeeDto> getWorkingShiftEmployeeByEmpIds(
      LocalDate startOfPeriod, LocalDate endOfPeriod, Set<Long> employeeIds) {
    List<WorkingShiftEmployeeDto> hoursWorkingShiftList;
    if (employeeIds != null && !employeeIds.isEmpty()) {
      hoursWorkingShiftList = workingShiftRepository.getWorkingShiftsDatesByEmpIds(
          startOfPeriod, endOfPeriod, employeeIds);
    } else {
      hoursWorkingShiftList =
          workingShiftRepository.getWorkingShiftsDates(startOfPeriod, endOfPeriod);
    }
    return hoursWorkingShiftList;
  }

  /**
   * Returns the count of employees who are currently on an active working shift.
   *
   * @return The count of employees on active working shifts.
   */
  public long getCountEmpsOnActiveWorkingShift() {
    return workingShiftRepository.countEmployeeOnActiveWorkingShift();
  }
}
