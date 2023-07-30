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
import java.time.LocalTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.trae.backend.entity.TimeControl;
import ru.trae.backend.entity.WorkingShift;
import ru.trae.backend.entity.user.Employee;
import ru.trae.backend.repository.TimeControlRepository;

/**
 * Service for managing time controls for employees.
 *
 * @author Vladimir Olennikov
 */
@Service
@RequiredArgsConstructor
public class TimeControlService {
  private final TimeControlRepository timeControlRepository;

  /**
   * Creates an arrival time control for an employee.
   *
   * @param e       The employee to generate the time control for.
   * @param ws      The working shift the employee is associated with.
   * @param onShift Whether the employee is on shift or not.
   * @param time    The time the employee arrived.
   * @return The created time control.
   */
  public TimeControl createArrivalTimeControl(Employee e, WorkingShift ws,
                                              boolean onShift, LocalDateTime time) {
    TimeControl tc = new TimeControl();
    tc.setArrival(time);
    tc.setDeparture(null);
    tc.setEmployee(e);
    tc.setOnShift(onShift);
    tc.setAutoClosingShift(false);
    tc.setWorkingShift(ws);

    return timeControlRepository.save(tc);
  }

  /**
   * Updates a TimeControl record in the repository with the given employee id and departure time.
   *
   * @param empId Employee id
   * @param time  Departure time
   */
  public void updateTimeControlForDeparture(Long empId, LocalDateTime time) {
    TimeControl tc = timeControlRepository
        .findByEmployeeIdAndIsOnShiftTrueAndWorkingShiftIsEndedFalse(empId);
    tc.setDeparture(time);
    tc.setOnShift(false);

    timeControlRepository.save(tc);
  }

  /**
   * Automatically closes a shift.
   *
   * @param tc The {@link TimeControl} to close the shift of.
   */
  public void autoClosingShift(TimeControl tc) {
    LocalTime specificTime = LocalTime.of(18, 0);

    tc.setOnShift(false);
    tc.setAutoClosingShift(true);
    tc.setDeparture(tc.getWorkingShift().getStartShift().with(specificTime));

    timeControlRepository.save(tc);
  }
}
