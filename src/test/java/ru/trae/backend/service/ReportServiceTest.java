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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.trae.backend.dto.employee.EmployeeIdFirstLastNameDto;
import ru.trae.backend.dto.mapper.ProjectForReportDtoMapper;
import ru.trae.backend.dto.project.ProjectForReportDto;
import ru.trae.backend.dto.report.DeadlineReq;
import ru.trae.backend.dto.report.ReportDeadlineDto;
import ru.trae.backend.dto.report.ReportProjectsForPeriodDto;
import ru.trae.backend.dto.report.ReportWorkingShiftForPeriodDto;
import ru.trae.backend.entity.task.Operation;
import ru.trae.backend.entity.task.Project;
import ru.trae.backend.projection.WorkingShiftEmployeeDto;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {
  @Mock
  private WorkingShiftService workingShiftService;

  @Mock
  private EmployeeService employeeService;

  @Mock
  private ProjectService projectService;

  @Mock
  private OperationService operationService;

  @Mock
  private ProjectForReportDtoMapper projectForReportDtoMapper;

  @InjectMocks
  private ReportService reportService;

  @Test
  void testReportWorkingShiftForPeriod() {
    //given
    LocalDate startOfPeriod = LocalDate.now();
    LocalDate endOfPeriod = LocalDate.now().plusDays(7);
    Set<Long> employeeIds = Set.of(1L, 2L, 3L);

    //when
    List<WorkingShiftEmployeeDto> workingShiftList = List.of();
    when(workingShiftService.getWorkingShiftEmployeeByEmpIds(startOfPeriod, endOfPeriod, employeeIds))
        .thenReturn(workingShiftList);

    List<EmployeeIdFirstLastNameDto> shortEmployeeDtoList = List.of(
        new EmployeeIdFirstLastNameDto(1L, "test_name_1", "test_lastname_1"),
        new EmployeeIdFirstLastNameDto(2L, "test_name_2", "test_lastname_2"),
        new EmployeeIdFirstLastNameDto(3L, "test_name_3", "test_lastname_3")
    );
    when(employeeService.getEmployeeDtoByListId(anyList())).thenReturn(shortEmployeeDtoList);

    ReportWorkingShiftForPeriodDto result = reportService.reportWorkingShiftForPeriod(startOfPeriod, endOfPeriod, employeeIds);

    //then
    assertNotNull(result);
    assertEquals(startOfPeriod, result.startPeriod());
    assertEquals(endOfPeriod, result.endPeriod());
    assertEquals(shortEmployeeDtoList, result.shortEmployeeDtoList());
    assertEquals(workingShiftList, result.workingShiftEmployeeDtoList());

    verify(workingShiftService).getWorkingShiftEmployeeByEmpIds(startOfPeriod, endOfPeriod, employeeIds);
    verify(employeeService).getEmployeeDtoByListId(anyList());
  }

  @Test
  void testReportProjectsForPeriod() {
    //given
    LocalDateTime startOfPeriod = LocalDateTime.now();
    LocalDateTime endOfPeriod = LocalDateTime.now().plusDays(7);
    int opPeriod = 60;

    Project p1 = new Project();
    p1.setId(1L);
    p1.setName("project_1");
    p1.setStartDate(startOfPeriod);
    p1.setEndDateInContract(endOfPeriod);
    p1.setOperationPeriod(opPeriod);

    Project p2 = new Project();
    p2.setId(2L);
    p2.setName("project_2");
    p2.setStartDate(startOfPeriod);
    p2.setEndDateInContract(endOfPeriod);
    p2.setOperationPeriod(opPeriod);

    Project p3 = new Project();
    p3.setId(3L);
    p3.setName("project_3");
    p3.setStartDate(startOfPeriod);
    p3.setEndDateInContract(endOfPeriod);
    p3.setOperationPeriod(opPeriod);

    List<Project> projects = List.of(p1, p2, p3);

    //when
    when(projectService.findProjectsForPeriod(
        startOfPeriod.toLocalDate(), endOfPeriod.toLocalDate())).thenReturn(projects);

    List<ProjectForReportDto> projectForReportDtoList = List.of(
        new ProjectForReportDto(1L, 100, "project_1", startOfPeriod,
            startOfPeriod.plusHours(2), endOfPeriod, endOfPeriod, endOfPeriod.minusDays(1),
            true, opPeriod, Collections.emptyList(), "test_customer", "test_comment"),
        new ProjectForReportDto(2L, 200, "project_2", startOfPeriod,
            startOfPeriod.plusHours(2), endOfPeriod, endOfPeriod, endOfPeriod.minusDays(1),
            true, opPeriod, Collections.emptyList(), "test_customer", "test_comment"),
        new ProjectForReportDto(3L, 300, "project_3", startOfPeriod,
            startOfPeriod.plusHours(2), endOfPeriod, endOfPeriod, endOfPeriod.minusDays(1),
            true, opPeriod, Collections.emptyList(), "test_customer", "test_comment")
    );

    when(projectForReportDtoMapper.apply(any(Project.class))).thenReturn(projectForReportDtoList.get(0),
        projectForReportDtoList.get(1), projectForReportDtoList.get(2));

    ReportProjectsForPeriodDto result = reportService.reportProjectsForPeriod(
        startOfPeriod.toLocalDate(), endOfPeriod.toLocalDate());

    //then
    assertNotNull(result);
    assertEquals(startOfPeriod.toLocalDate(), result.startPeriod());
    assertEquals(endOfPeriod.toLocalDate(), result.endPeriod());
    assertEquals(projectForReportDtoList, result.projectsForReportDtoList());

    verify(projectService).findProjectsForPeriod(
        startOfPeriod.toLocalDate(), endOfPeriod.toLocalDate());
    verify(projectForReportDtoMapper, times(projects.size())).apply(any(Project.class));
  }
}
