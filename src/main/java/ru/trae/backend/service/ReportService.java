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
import ru.trae.backend.dto.employee.ShortEmployeeDto;
import ru.trae.backend.projection.WorkingShiftEmployeePercentage;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportService {
  private final WorkingShiftService workingShiftService;
  private final EmployeeService employeeService;
  
  public void report(LocalDate startOfPeriod, LocalDate endOfPeriod) {
    List<WorkingShiftEmployeePercentage> percentage = workingShiftService.getWorkingShiftEmployeePercentage(startOfPeriod, endOfPeriod);
    List<ShortEmployeeDto> shortEmployeeDtoList = employeeService.getShortEmployeeDtoByListId(percentage.stream().map(WorkingShiftEmployeePercentage::getEmployeeId).distinct().toList());
  }
}
