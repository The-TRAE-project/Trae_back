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
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.trae.backend.dto.WorkingShiftDto;
import ru.trae.backend.dto.mapper.WorkingShiftDtoMapper;
import ru.trae.backend.entity.TimeControl;
import ru.trae.backend.entity.WorkingShift;
import ru.trae.backend.entity.user.Employee;
import ru.trae.backend.exceptionhandler.exception.WorkingShiftException;
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
  public void closeWorkingShift() {
    if (!existsActiveWorkingShift()) {
      return;
    }

    WorkingShift ws = workingShiftRepository.findByIsEndedFalse();

    ws.getTimeControls().stream()
        .filter(TimeControl::isOnShift)
        .forEach(timeControlService::autoClosingShift);

    ws.setEnded(true);
    ws.setEndShift(LocalDateTime.now());

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
   * Checks if an employee is on shift.
   *
   * @param isOnShift the status of the employee.
   * @param empId     the id of the employee.
   * @return true if the employee is on shift; false otherwise.
   */
  public boolean employeeOnShift(boolean isOnShift, long empId) {
    return workingShiftRepository.existsEmpOnShift(isOnShift, empId);
  }
}
