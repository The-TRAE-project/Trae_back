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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.spy;
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
import ru.trae.backend.dto.report.ReportDashboardStatsDto;
import ru.trae.backend.dto.report.ReportDeadlineDto;
import ru.trae.backend.dto.report.ReportProjectsForPeriodDto;
import ru.trae.backend.dto.report.ReportWorkingShiftForPeriodDto;
import ru.trae.backend.dto.report.SecondResponseSubDto;
import ru.trae.backend.dto.report.ThirdResponseSubDto;
import ru.trae.backend.entity.task.Operation;
import ru.trae.backend.entity.task.Project;
import ru.trae.backend.entity.user.Employee;
import ru.trae.backend.exceptionhandler.exception.ReportException;
import ru.trae.backend.projection.WorkingShiftEmployeeDto;
import ru.trae.backend.util.ReportParameter;

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
  void testReportWorkingShiftForPeriodWithoutEmpIds() {
    //given
    LocalDate startOfPeriod = LocalDate.now();
    LocalDate endOfPeriod = LocalDate.now().plusDays(7);

    //when
    List<WorkingShiftEmployeeDto> workingShiftList = List.of();
    when(workingShiftService.getWorkingShiftEmployeeByEmpIds(startOfPeriod, endOfPeriod, null))
        .thenReturn(workingShiftList);

    List<EmployeeIdFirstLastNameDto> shortEmployeeDtoList = List.of(
        new EmployeeIdFirstLastNameDto(1L, "test_name_1", "test_lastname_1"),
        new EmployeeIdFirstLastNameDto(2L, "test_name_2", "test_lastname_2"),
        new EmployeeIdFirstLastNameDto(3L, "test_name_3", "test_lastname_3")
    );
    when(employeeService.getEmployeeDtoByListId(anyList())).thenReturn(shortEmployeeDtoList);

    ReportWorkingShiftForPeriodDto result = reportService.reportWorkingShiftForPeriod(startOfPeriod, endOfPeriod, null);

    //then
    assertNotNull(result);
    assertEquals(startOfPeriod, result.startPeriod());
    assertEquals(endOfPeriod, result.endPeriod());
    assertEquals(shortEmployeeDtoList, result.shortEmployeeDtoList());
    assertEquals(workingShiftList, result.workingShiftEmployeeDtoList());

    verify(workingShiftService).getWorkingShiftEmployeeByEmpIds(startOfPeriod, endOfPeriod, null);
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

  @Test
  void reportDeadlines_ProjectAsFirstParameter_OperationAsSecondParameter_GeneratesReportWithCorrectValues() {
    //given
    DeadlineReq req = new DeadlineReq(
        ReportParameter.PROJECT, 1L,
        ReportParameter.OPERATION, Collections.singleton(2L),
        ReportParameter.EMPLOYEE, Collections.singleton(3L));

    Project p = new Project();
    p.setId(1L);
    p.setNumber(100);
    Employee e = new Employee();
    e.setId(3L);
    e.setLastName("employee_last_name");
    Operation o = new Operation();
    o.setId(2L);
    o.setEmployee(e);
    o.setProject(p);
    o.setName("operation_name");
    o.setPlannedEndDate(LocalDateTime.now().minusDays(1));
    o.setRealEndDate(LocalDateTime.now().minusDays(2));
    p.setOperations(List.of(o));

    List<Operation> operations = Collections.singletonList(o);

    //when
    when(operationService.getOperationsByIds(anySet())).thenReturn(operations);

    ReportDeadlineDto report = reportService.reportDeadlines(req);

    //then
    assertEquals(1L, report.getFirstRespId());
    assertEquals("100", report.getFirstRespValue());

    List<SecondResponseSubDto> secondRespValues = report.getSecondRespValues();
    assertEquals(1, secondRespValues.size());

    SecondResponseSubDto secondResponseSubDto = secondRespValues.get(0);
    assertEquals(2L, secondResponseSubDto.secondRespId());
    assertEquals("operation_name", secondResponseSubDto.secondRespValue());

    List<ThirdResponseSubDto> thirdRespValues = secondResponseSubDto.thirdRespValues();
    assertEquals(1, thirdRespValues.size());

    ThirdResponseSubDto thirdResponseSubDto = thirdRespValues.get(0);
    assertEquals(3L, thirdResponseSubDto.thirdRespId());
    assertEquals("employee_last_name", thirdResponseSubDto.thirdRespValue());

    verify(operationService, times(1)).getOperationsByIds(anySet());
  }

  @Test
  void reportDeadlines_ProjectAsFirstParameter_EmployeeAsSecondParameter_GeneratesReportWithCorrectValues() {
    //given
    DeadlineReq req = new DeadlineReq(
        ReportParameter.PROJECT, 1L,
        ReportParameter.EMPLOYEE, Collections.singleton(2L),
        ReportParameter.OPERATION, Collections.singleton(3L));

    Project p = new Project();
    p.setId(1L);
    p.setNumber(100);
    Employee e = new Employee();
    e.setId(2L);
    e.setLastName("employee_last_name");
    Operation o = new Operation();
    o.setId(3L);
    o.setEmployee(e);
    o.setProject(p);
    o.setName("operation_name");
    o.setPlannedEndDate(LocalDateTime.now().minusDays(1));
    o.setRealEndDate(LocalDateTime.now().minusDays(2));
    p.setOperations(List.of(o));

    List<Operation> operations = Collections.singletonList(o);

    //when
    when(operationService.getOperationsByIds(anySet())).thenReturn(operations);

    ReportDeadlineDto report = reportService.reportDeadlines(req);

    //then
    assertEquals(1L, report.getFirstRespId());
    assertEquals("100", report.getFirstRespValue());

    List<SecondResponseSubDto> secondRespValues = report.getSecondRespValues();
    assertEquals(1, secondRespValues.size());

    SecondResponseSubDto secondResponseSubDto = secondRespValues.get(0);
    assertEquals(2L, secondResponseSubDto.secondRespId());
    assertEquals("employee_last_name", secondResponseSubDto.secondRespValue());

    List<ThirdResponseSubDto> thirdRespValues = secondResponseSubDto.thirdRespValues();
    assertEquals(1, thirdRespValues.size());

    ThirdResponseSubDto thirdResponseSubDto = thirdRespValues.get(0);
    assertEquals(3L, thirdResponseSubDto.thirdRespId());
    assertEquals("operation_name", thirdResponseSubDto.thirdRespValue());

    verify(operationService, times(1)).getOperationsByIds(anySet());
  }

  @Test
  void reportDeadlines_InvalidRequest_ThrowsReportException() {
    //given
    DeadlineReq req = new DeadlineReq(
        null, 1,
        null, null,
        null, null);

    //then
    assertThrows(ReportException.class, () -> reportService.reportDeadlines(req));
  }

  @Test
  void reportDeadlines_OperationAsFirstParameter_ProjectAsSecondParameter_GeneratesReportWithCorrectValues() {
    //given
    DeadlineReq req = new DeadlineReq(
        ReportParameter.OPERATION, 4L,
        ReportParameter.PROJECT, Collections.singleton(5L),
        ReportParameter.EMPLOYEE, Collections.singleton(6L));

    Project p = new Project();
    p.setId(5L);
    p.setNumber(100);
    Employee e = new Employee();
    e.setId(6L);
    e.setLastName("employee_last_name");
    Operation o = new Operation();
    o.setId(4L);
    o.setEmployee(e);
    o.setProject(p);
    o.setName("operation_name");
    o.setPlannedEndDate(LocalDateTime.now().minusDays(1));
    o.setRealEndDate(LocalDateTime.now().minusDays(2));
    p.setOperations(List.of(o));

    List<Operation> operations = Collections.singletonList(o);

    //when
    when(operationService.getOperationsByIds(anySet())).thenReturn(operations);

    ReportDeadlineDto report = reportService.reportDeadlines(req);

    //then
    assertEquals(4L, report.getFirstRespId());
    assertEquals("operation_name", report.getFirstRespValue());

    List<SecondResponseSubDto> secondRespValues = report.getSecondRespValues();
    assertEquals(1, secondRespValues.size());

    SecondResponseSubDto secondResponseSubDto = secondRespValues.get(0);
    assertEquals(5L, secondResponseSubDto.secondRespId());
    assertEquals("100", secondResponseSubDto.secondRespValue());

    List<ThirdResponseSubDto> thirdRespValues = secondResponseSubDto.thirdRespValues();
    assertEquals(1, thirdRespValues.size());

    ThirdResponseSubDto thirdResponseSubDto = thirdRespValues.get(0);
    assertEquals(6L, thirdResponseSubDto.thirdRespId());
    assertEquals("employee_last_name", thirdResponseSubDto.thirdRespValue());

    verify(operationService, times(1)).getOperationsByIds(anySet());
  }

  @Test
  void reportDeadlines_OperationAsFirstParameter_EmployeeAsSecondParameter_GeneratesReportWithCorrectValues() {
    //given
    DeadlineReq req = new DeadlineReq(
        ReportParameter.OPERATION, 4L,
        ReportParameter.EMPLOYEE, Collections.singleton(6L),
        ReportParameter.PROJECT, Collections.singleton(5L));

    Project p = new Project();
    p.setId(6L);
    p.setNumber(100);
    Employee e = new Employee();
    e.setId(5L);
    e.setLastName("employee_last_name");
    Operation o = new Operation();
    o.setId(4L);
    o.setEmployee(e);
    o.setProject(p);
    o.setName("operation_name");
    o.setPlannedEndDate(LocalDateTime.now().minusDays(1));
    o.setRealEndDate(LocalDateTime.now().minusDays(2));
    p.setOperations(List.of(o));

    List<Operation> operations = Collections.singletonList(o);

    //when
    when(operationService.getOperationsByIds(anySet())).thenReturn(operations);

    ReportDeadlineDto report = reportService.reportDeadlines(req);

    //then
    assertEquals(4L, report.getFirstRespId());
    assertEquals("operation_name", report.getFirstRespValue());

    List<SecondResponseSubDto> secondRespValues = report.getSecondRespValues();
    assertEquals(1, secondRespValues.size());

    SecondResponseSubDto secondResponseSubDto = secondRespValues.get(0);
    assertEquals(5L, secondResponseSubDto.secondRespId());
    assertEquals("employee_last_name", secondResponseSubDto.secondRespValue());

    List<ThirdResponseSubDto> thirdRespValues = secondResponseSubDto.thirdRespValues();
    assertEquals(1, thirdRespValues.size());

    ThirdResponseSubDto thirdResponseSubDto = thirdRespValues.get(0);
    assertEquals(6L, thirdResponseSubDto.thirdRespId());
    assertEquals("100", thirdResponseSubDto.thirdRespValue());

    verify(operationService, times(1)).getOperationsByIds(anySet());
  }

  @Test
  void reportDeadlines_EmployeeAsFirstParameter_ProjectAsSecondParameter_GeneratesReportWithCorrectValues() {
    //given
    DeadlineReq req = new DeadlineReq(
        ReportParameter.EMPLOYEE, 7L,
        ReportParameter.PROJECT, Collections.singleton(8L),
        ReportParameter.OPERATION, Collections.singleton(9L));

    Project p = new Project();
    p.setId(8L);
    p.setNumber(100);
    Employee e = new Employee();
    e.setId(7L);
    e.setLastName("employee_last_name");
    Operation o = new Operation();
    o.setId(9L);
    o.setEmployee(e);
    o.setProject(p);
    o.setName("operation_name");
    o.setPlannedEndDate(LocalDateTime.now().minusDays(1));
    o.setRealEndDate(LocalDateTime.now().minusDays(2));
    p.setOperations(List.of(o));

    List<Operation> operations = Collections.singletonList(o);

    //when
    when(operationService.getOperationsByIds(anySet())).thenReturn(operations);

    ReportDeadlineDto report = reportService.reportDeadlines(req);

    //then
    assertEquals(7L, report.getFirstRespId());
    assertEquals("employee_last_name", report.getFirstRespValue());

    List<SecondResponseSubDto> secondRespValues = report.getSecondRespValues();
    assertEquals(1, secondRespValues.size());

    SecondResponseSubDto secondResponseSubDto = secondRespValues.get(0);
    assertEquals(8L, secondResponseSubDto.secondRespId());
    assertEquals("100", secondResponseSubDto.secondRespValue());

    List<ThirdResponseSubDto> thirdRespValues = secondResponseSubDto.thirdRespValues();
    assertEquals(1, thirdRespValues.size());

    ThirdResponseSubDto thirdResponseSubDto = thirdRespValues.get(0);
    assertEquals(9L, thirdResponseSubDto.thirdRespId());
    assertEquals("operation_name", thirdResponseSubDto.thirdRespValue());

    verify(operationService, times(1)).getOperationsByIds(anySet());
  }

  @Test
  void reportDeadlines_EmployeeAsFirstParameter_OperationAsSecondParameter_GeneratesReportWithCorrectValues() {
    //given
    DeadlineReq req = new DeadlineReq(
        ReportParameter.EMPLOYEE, 7L,
        ReportParameter.OPERATION, Collections.singleton(8L),
        ReportParameter.PROJECT, Collections.singleton(9L));

    Project p = new Project();
    p.setId(9L);
    p.setNumber(100);
    Employee e = new Employee();
    e.setId(7L);
    e.setLastName("employee_last_name");
    Operation o = new Operation();
    o.setId(8L);
    o.setEmployee(e);
    o.setProject(p);
    o.setName("operation_name");
    o.setPlannedEndDate(LocalDateTime.now().minusDays(1));
    o.setRealEndDate(LocalDateTime.now().minusDays(2));
    p.setOperations(List.of(o));

    List<Operation> operations = Collections.singletonList(o);

    //when
    when(operationService.getOperationsByIds(anySet())).thenReturn(operations);

    ReportDeadlineDto report = reportService.reportDeadlines(req);

    //then
    assertEquals(7L, report.getFirstRespId());
    assertEquals("employee_last_name", report.getFirstRespValue());

    List<SecondResponseSubDto> secondRespValues = report.getSecondRespValues();
    assertEquals(1, secondRespValues.size());

    SecondResponseSubDto secondResponseSubDto = secondRespValues.get(0);
    assertEquals(8L, secondResponseSubDto.secondRespId());
    assertEquals("operation_name", secondResponseSubDto.secondRespValue());

    List<ThirdResponseSubDto> thirdRespValues = secondResponseSubDto.thirdRespValues();
    assertEquals(1, thirdRespValues.size());

    ThirdResponseSubDto thirdResponseSubDto = thirdRespValues.get(0);
    assertEquals(9L, thirdResponseSubDto.thirdRespId());
    assertEquals("100", thirdResponseSubDto.thirdRespValue());

    verify(operationService, times(1)).getOperationsByIds(anySet());
  }

  @Test
  void reportDeadlines_IncorrectParameters_ThrowsReportException() {
    //given
    DeadlineReq req = new DeadlineReq(
        ReportParameter.PROJECT, 1L,
        ReportParameter.PROJECT, Collections.singleton(2L),
        ReportParameter.OPERATION, Collections.singleton(3L));

    //then
    assertThrows(ReportException.class, () -> reportService.reportDeadlines(req));
  }

  @Test
  void reportDeadlines_ProjectAsFirstParameter_NoOperationsFound_ThrowsReportException() {
    //given
    DeadlineReq req = new DeadlineReq(
        ReportParameter.PROJECT, 1L,
        ReportParameter.OPERATION, Collections.singleton(2L),
        ReportParameter.EMPLOYEE, Collections.singleton(3L));

    //when
    when(operationService.getOperationsByIds(anySet())).thenReturn(Collections.emptyList());

    //then
    assertThrows(ReportException.class, () -> reportService.reportDeadlines(req));
  }

  @Test
  void reportDeadlines_OperationAsFirstParameter_NoOperationsFound_ThrowsReportException() {
    //given
    DeadlineReq req = new DeadlineReq(
        ReportParameter.OPERATION, 4L,
        ReportParameter.PROJECT, Collections.singleton(5L),
        ReportParameter.EMPLOYEE, Collections.singleton(6L));

    //when
    when(operationService.getOperationsByIds(anySet())).thenReturn(Collections.emptyList());

    //then
    assertThrows(ReportException.class, () -> reportService.reportDeadlines(req));
  }

  @Test
  void reportDeadlines_EmployeeAsFirstParameter_NoOperationsFound_ThrowsReportException() {
    //given
    DeadlineReq req = new DeadlineReq(
        ReportParameter.EMPLOYEE, 7L,
        ReportParameter.PROJECT, Collections.singleton(8L),
        ReportParameter.OPERATION, Collections.singleton(9L));

    //when
    when(operationService.getOperationsByIds(anySet())).thenReturn(Collections.emptyList());

    //then
    assertThrows(ReportException.class, () -> reportService.reportDeadlines(req));
  }

  @Test
  void reportDeadlines_ProjectAsFirstParameter_OperationWithoutEmployee_ThrowsReportException() {
    //given
    DeadlineReq req = new DeadlineReq(
        ReportParameter.PROJECT, 1L,
        ReportParameter.OPERATION, Collections.singleton(2L),
        ReportParameter.EMPLOYEE, Collections.singleton(3L));

    Project p = new Project();
    p.setId(1L);
    p.setNumber(100);
    Operation o = new Operation();
    o.setId(2L);
    o.setEmployee(null);
    o.setProject(p);
    o.setName("operation_name");
    o.setPlannedEndDate(LocalDateTime.now().minusDays(1));
    o.setRealEndDate(LocalDateTime.now().minusDays(2));
    p.setOperations(List.of(o));

    List<Operation> operations = Collections.singletonList(o);

    //when
    when(operationService.getOperationsByIds(anySet())).thenReturn(operations);

    //then
    assertThrows(ReportException.class, () -> reportService.reportDeadlines(req));
  }

  @Test
  void reportDeadlines_EmployeeAsFirstParameter_OperationWithoutEmployee_ThrowsReportException() {
    //given
    DeadlineReq req = new DeadlineReq(
        ReportParameter.EMPLOYEE, 7L,
        ReportParameter.PROJECT, Collections.singleton(8L),
        ReportParameter.OPERATION, Collections.singleton(9L));

    Project p = new Project();
    p.setId(1L);
    p.setNumber(100);
    Operation o = new Operation();
    o.setId(2L);
    o.setEmployee(null);
    o.setProject(p);
    o.setName("operation_name");
    o.setPlannedEndDate(LocalDateTime.now().minusDays(1));
    o.setRealEndDate(LocalDateTime.now().minusDays(2));
    p.setOperations(List.of(o));

    List<Operation> operations = Collections.singletonList(o);

    //when
    when(operationService.getOperationsByIds(anySet())).thenReturn(operations);

    //then
    assertThrows(ReportException.class, () -> reportService.reportDeadlines(req));
  }

  @Test
  void reportDeadlines_ProjectAsFirstParameter_OperationWithIncorrectProjectId_ThrowsReportException() {
    //given
    DeadlineReq req = new DeadlineReq(
        ReportParameter.PROJECT, 1L,
        ReportParameter.OPERATION, Collections.singleton(2L),
        ReportParameter.EMPLOYEE, Collections.singleton(3L));

    Project p = new Project();
    p.setId(10L);
    p.setNumber(100);
    Operation o = new Operation();
    o.setId(2L);
    o.setEmployee(null);
    o.setProject(p);
    o.setName("operation_name");
    o.setPlannedEndDate(LocalDateTime.now().minusDays(1));
    o.setRealEndDate(LocalDateTime.now().minusDays(2));
    p.setOperations(List.of(o));

    List<Operation> operations = Collections.singletonList(o);

    //when
    when(operationService.getOperationsByIds(anySet())).thenReturn(operations);

    //then
    assertThrows(ReportException.class, () -> reportService.reportDeadlines(req));
  }

  @Test
  void reportDeadlines_OperationAsFirstParameter_ProjectWithIncorrectOperationId_ThrowsReportException() {
    //given
    DeadlineReq req = new DeadlineReq(
        ReportParameter.OPERATION, 4L,
        ReportParameter.PROJECT, Collections.singleton(5L),
        ReportParameter.EMPLOYEE, Collections.singleton(6L));

    Project p = new Project();
    p.setId(11L);
    p.setNumber(100);
    Operation o = new Operation();
    o.setId(2L);
    o.setEmployee(null);
    o.setProject(p);
    o.setName("operation_name");
    o.setPlannedEndDate(LocalDateTime.now().minusDays(1));
    o.setRealEndDate(LocalDateTime.now().minusDays(2));
    p.setOperations(List.of(o));

    List<Operation> operations = Collections.singletonList(new Operation());

    //when
    when(operationService.getOperationsByIds(anySet())).thenReturn(operations);


    //then
    assertThrows(ReportException.class, () -> reportService.reportDeadlines(req));
  }

  @Test
  void reportDeadlines_EmployeeAsFirstParameter_IncompleteEmployeeData_ThrowsReportException() {
    //given
    DeadlineReq req = new DeadlineReq(
        ReportParameter.EMPLOYEE, 7L,
        ReportParameter.PROJECT, Collections.singleton(8L),
        ReportParameter.OPERATION, Collections.singleton(9L));

    Project p = new Project();
    p.setId(12L);
    p.setNumber(100);
    Operation o = new Operation();
    o.setId(2L);
    o.setEmployee(null);
    o.setProject(p);
    o.setName("operation_name");
    o.setPlannedEndDate(LocalDateTime.now().minusDays(1));
    o.setRealEndDate(LocalDateTime.now().minusDays(2));
    p.setOperations(List.of(o));

    List<Operation> operations = Collections.singletonList(new Operation());

    //when
    when(operationService.getOperationsByIds(anySet())).thenReturn(operations);

    //then
    assertThrows(ReportException.class, () -> reportService.reportDeadlines(req));
  }

  @Test
  void testReportDashboard() {
    //given
    long expectedNotEndedProjects = 1;
    long expectedProjectsWithOverdueCurrentOperation = 2;
    long expectedOverdueProjects = 3;
    long expectedProjectsWithLastOpReadyToAcceptance = 4;

    //when
    when(projectService.getCountNotEndedProjects()).thenReturn(expectedNotEndedProjects);
    when(projectService.getCountOverdueProjects()).thenReturn(expectedOverdueProjects);
    when(projectService.getCountProjectsWithOverdueCurrentOperation())
        .thenReturn(expectedProjectsWithOverdueCurrentOperation);
    when(projectService.getCountProjectsWithLastOpReadyToAcceptance())
        .thenReturn(expectedProjectsWithLastOpReadyToAcceptance);
    ReportDashboardStatsDto dto = reportService.getDashboardStatsDto();

    //then
    assertEquals(expectedNotEndedProjects, dto.countNotEndedProjects());
    assertEquals(expectedProjectsWithOverdueCurrentOperation,
        dto.countProjectsWithOverdueCurrentOperation());
    assertEquals(expectedOverdueProjects, dto.countOverdueProjects());
    assertEquals(expectedProjectsWithLastOpReadyToAcceptance,
        dto.countProjectsWithLastOpReadyToAcceptance());
  }
}
