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
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.trae.backend.dto.employee.EmployeeIdFirstLastNameDto;
import ru.trae.backend.dto.employee.EmployeeIdTotalPartsDto;
import ru.trae.backend.dto.mapper.ProjectForReportDtoMapper;
import ru.trae.backend.dto.project.ProjectForReportDto;
import ru.trae.backend.dto.report.ReportProjectsForPeriodDto;
import ru.trae.backend.dto.report.ReportWorkingShiftForPeriodDto;
import ru.trae.backend.entity.task.Project;
import ru.trae.backend.exceptionhandler.exception.ReportException;
import ru.trae.backend.projection.WorkingShiftEmployeeDto;

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
  private final ProjectService projectService;
  private final ProjectForReportDtoMapper projectForReportDtoMapper;
  
  /**
   * Generates a report of working shifts for a specific period.
   *
   * @param startOfPeriod The start date of the period.
   * @param endOfPeriod   The end date of the period.
   * @param employeeIds   The set of concrete employee ids
   * @return The {@link ReportWorkingShiftForPeriodDto} containing the report data.
   */
  public ReportWorkingShiftForPeriodDto reportWorkingShiftForPeriod(
      LocalDate startOfPeriod, LocalDate endOfPeriod, Set<Long> employeeIds) {
    
    checkStartEndDates(startOfPeriod, endOfPeriod);
    
    List<WorkingShiftEmployeeDto> workingShiftList =
        workingShiftService.getWorkingShiftEmployeeByEmpIds(
            startOfPeriod, endOfPeriod, employeeIds);
    
    List<EmployeeIdFirstLastNameDto> shortEmployeeDtoList = employeeService.getEmployeeDtoByListId(
        workingShiftList.stream()
            .map(WorkingShiftEmployeeDto::getEmployeeId)
            .distinct()
            .toList());
    
    List<EmployeeIdTotalPartsDto> employeeIdTotalPartsDtoList = workingShiftList.stream()
        .collect(Collectors.groupingBy(WorkingShiftEmployeeDto::getEmployeeId,
            Collectors.summingDouble(WorkingShiftEmployeeDto::getPartOfShift)))
        .entrySet()
        .stream()
        .map(e -> new EmployeeIdTotalPartsDto(e.getKey(), e.getValue().floatValue()))
        .toList();
    
    return new ReportWorkingShiftForPeriodDto(
        startOfPeriod,
        endOfPeriod,
        shortEmployeeDtoList,
        workingShiftList,
        employeeIdTotalPartsDtoList);
  }
  
  /**
   * Generates a report of projects for a given period.
   *
   * @param startOfPeriod The start date of the period.
   * @param endOfPeriod   The end date of the period.
   * @return A DTO (Data Transfer Object) representing the report for the specified period.
   */
  public ReportProjectsForPeriodDto reportProjectsForPeriod(
      LocalDate startOfPeriod, LocalDate endOfPeriod) {
    
    checkStartEndDates(startOfPeriod, endOfPeriod);
    
    List<Project> projects = projectService.findProjectsForPeriod(startOfPeriod, endOfPeriod);
    List<ProjectForReportDto> projectForReportDtoList = projects.stream()
        .map(projectForReportDtoMapper)
        .toList();
    
    return new ReportProjectsForPeriodDto(
        startOfPeriod, endOfPeriod, LocalDate.now(), projectForReportDtoList);
  }
  
  private void checkStartEndDates(LocalDate startOfPeriod, LocalDate endOfPeriod) {
    if (startOfPeriod.isAfter(endOfPeriod)) {
      throw new ReportException(HttpStatus.BAD_REQUEST, "Start date cannot be after end date.");
    }
  }
}
