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
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.trae.backend.dto.employee.EmployeeIdFirstLastNameDto;
import ru.trae.backend.dto.report.ReportWorkingShiftForPeriodDto;
import ru.trae.backend.projection.WorkingShiftEmployeeHours;

/**
 * Service class for generating reports.
 *
 * @author Vladimir Olennikov
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReportService {
  private final WorkingShiftService workingShiftService;
  private final EmployeeService employeeService;
  
  /**
   * Generates a report of working shifts for a specific period.
   *
   * @param startOfPeriod The start date of the period.
   * @param endOfPeriod   The end date of the period.
   * @return The {@link ReportWorkingShiftForPeriodDto} containing the report data.
   */
  public ReportWorkingShiftForPeriodDto reportWorkingShiftForPeriod(
      LocalDate startOfPeriod, LocalDate endOfPeriod) {
    List<WorkingShiftEmployeeHours> hoursWorkingShiftList =
        workingShiftService.getWorkingShiftEmployeeHours(startOfPeriod, endOfPeriod);
    List<EmployeeIdFirstLastNameDto> shortEmployeeDtoList = employeeService.getEmployeeDtoByListId(
        hoursWorkingShiftList.stream()
            .map(WorkingShiftEmployeeHours::getEmployeeId)
            .distinct()
            .toList());
    
    return new ReportWorkingShiftForPeriodDto(
        startOfPeriod, endOfPeriod, shortEmployeeDtoList, hoursWorkingShiftList);
  }
}
