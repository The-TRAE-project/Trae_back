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

import static ru.trae.backend.util.Constant.NOT_FOUND_CONST;
import static ru.trae.backend.util.Constant.OPERATION_WITH_ID;
import static ru.trae.backend.util.Constant.PROJECT_WITH_ID;
import static ru.trae.backend.util.Constant.WRONG_PARAMETER;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
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
  private final OperationService operationService;
  private final ProjectForReportDtoMapper projectForReportDtoMapper;

  /**
   * Retrieves the dashboard statistics for the report.
   *
   * @return A {@link ReportDashboardStatsDto} object containing the dashboard statistics.
   */
  public ReportDashboardStatsDto getDashboardStatsDto() {
    return new ReportDashboardStatsDto(
        workingShiftService.getCountEmpsOnActiveWorkingShift(),
        projectService.getCountNotEndedProjects(),
        projectService.getCountProjectsWithOverdueCurrentOperation(),
        projectService.getCountOverdueProjects(),
        projectService.getCountProjectsWithLastOpReadyToAcceptance()
    );
  }

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

    List<EmployeeIdFirstLastNameDto> shortEmployeeDtoList;
    if (employeeIds == null || employeeIds.isEmpty()) {
      shortEmployeeDtoList = employeeService.getEmployeeDtoByListId(
          workingShiftList.stream()
              .map(WorkingShiftEmployeeDto::getEmployeeId)
              .distinct()
              .toList());
    } else {
      shortEmployeeDtoList = employeeService.getEmployeeDtoByListId(
          employeeIds.stream()
              .toList());
    }

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

  /**
   * Generates a report containing deadlines based on the provided request.
   *
   * @param req The request object specifying the parameters for the report.
   * @return A ReportDeadlineDto object containing the generated report.
   * @throws ReportException if there is an error generating the report.
   */
  public ReportDeadlineDto reportDeadlines(DeadlineReq req) {

    //проверка на неповторяющиеся значения параметров
    checkCorrectParametersRequest(req);

    ReportDeadlineDto report = new ReportDeadlineDto();
    //здесь присваивается id основному блоку отчета согласно id из значения первого параметра
    report.setFirstRespId(req.valueOfFirstParameter());

    //выборка из базы данных для отчета всегда берется согласно списку операций в запросе
    //в этом месте происходит поиск в каком из параметров указан список операций
    List<Operation> ops;
    if (req.firstParameter().ordinal() == 1) {
      ops = operationService.getOperationsByIds(Set.of(req.valueOfFirstParameter()));
    } else if (req.secondParameter().ordinal() == 1) {
      ops = operationService.getOperationsByIds(req.valuesOfSecondParameter());
    } else {
      ops = operationService.getOperationsByIds(req.valuesOfThirdParameter());
    }

    checkNotEmptyListOps(ops);

    switch (req.firstParameter()) {

      //кейс, где проект является главным блоком в отчете
      case PROJECT -> {
        //проверка на соответствие id проекта из запроса и id проекта из выборки операций
        ops.forEach(o -> checkCorrectProjectIdFromReqAndOp(req.valueOfFirstParameter(), o));

        report.setFirstRespValue(String.valueOf(ops.get(0).getProject().getNumber()));
        switch (req.secondParameter()) {
          //кейс, где операции являются вторым блоком в отчете по проекту
          case OPERATION -> addToPrReportSecondSubDtoByOperations(req.valueOfFirstParameter(),
              req.valuesOfSecondParameter(), req.valuesOfThirdParameter(), report, ops);
          //кейс, где сотрудники являются вторым блоком в отчете по проекту
          case EMPLOYEE -> addToPrReportSecondSubDtoByEmployees(req.valueOfFirstParameter(),
              req.valuesOfSecondParameter(), req.valuesOfThirdParameter(), report, ops);
          default -> throw new ReportException(HttpStatus.BAD_REQUEST, WRONG_PARAMETER.value);
        }
      }

      //кейс, где операция является главным блоком в отчете
      case OPERATION -> {
        report.setFirstRespValue(ops.get(0).getName());
        switch (req.secondParameter()) {
          //кейс, где проекты являются вторым блоком в отчете по операции
          case PROJECT -> addToOpReportSecondSubDtoByProject(
              req.valuesOfSecondParameter(), req.valuesOfThirdParameter(), report, ops.get(0));
          //кейс, где сотрудники являются вторым блоком в отчете по операции
          case EMPLOYEE -> addToOpReportSecondSubDtoByEmployee(
              req.valuesOfSecondParameter(), req.valuesOfThirdParameter(), report, ops.get(0));
          default -> throw new ReportException(HttpStatus.BAD_REQUEST, WRONG_PARAMETER.value);
        }
      }

      //кейс, где сотрудник является главным блоком в отчете
      case EMPLOYEE -> {
        //проверка на то, что во всех операциях из выборки есть сотрудник
        ops.forEach(this::checkNotNullEmpInOp);
        //проверка на соответствие id сотрудника из запроса с id сотрудника из выборки операций
        ops.forEach(o -> checkCorrectEmpIdFromReqAndEmpIdFromOp(req.valueOfFirstParameter(), o));

        report.setFirstRespValue(ops.get(0).getEmployee().getLastName());
        switch (req.secondParameter()) {
          //кейс, где проекты являются вторым блоком в отчете по сотруднику
          case PROJECT -> addToEmpReportSecondSubDtoByProjects(req.valueOfFirstParameter(),
              req.valuesOfSecondParameter(), req.valuesOfThirdParameter(), report, ops);
          //кейс, где операции являются вторым блоком в отчете по сотруднику
          case OPERATION -> addToEmpReportSecondSubDtoByOperations(req.valueOfFirstParameter(),
              req.valuesOfSecondParameter(), req.valuesOfThirdParameter(), report, ops);
          default -> throw new ReportException(HttpStatus.BAD_REQUEST, WRONG_PARAMETER.value);
        }
      }

      default -> throw new ReportException(HttpStatus.BAD_REQUEST, "Wrong values in parameters");
    }

    return report;
  }

  private void checkCorrectProjectIdFromReqAndOp(long projectIdFromReq, Operation o) {
    if (projectIdFromReq != o.getProject().getId()) {
      throw new ReportException(HttpStatus.BAD_REQUEST,
          PROJECT_WITH_ID.value + projectIdFromReq + " does not match the project id: "
              + o.getProject().getId() + " from the operation");
    }
  }

  private void checkCorrectEmpIdFromReqAndEmpIdFromOp(long employeeId, Operation o) {
    if (o.getEmployee().getId() != employeeId) {
      throw new ReportException(HttpStatus.BAD_REQUEST, OPERATION_WITH_ID.value + o.getId()
          + " from the selection does not match the specified employee with id: " + employeeId);
    }
  }

  private void checkNotNullEmpInOp(Operation o) {
    if (o.getEmployee() == null) {
      throw new ReportException(HttpStatus.BAD_REQUEST,
          "One of the operations from the selection does not have an employee");
    }
  }

  private void checkNotEmptyListOps(List<Operation> ops) {
    if (ops.isEmpty()) {
      throw new ReportException(HttpStatus.BAD_REQUEST,
          "The parameter values are not correct, the final result is empty");
    }
  }

  private void checkCorrectParametersRequest(DeadlineReq req) {
    Set<ReportParameter> reqSet = new HashSet<>();
    reqSet.add(req.firstParameter());
    reqSet.add(req.secondParameter());
    reqSet.add(req.thirdParameter());

    if (reqSet.size() != 3) {
      throw new ReportException(HttpStatus.CONFLICT, "Parameter values are repeated");
    }
  }

  private void addToPrReportSecondSubDtoByEmployees(
      Long firstValue,
      Set<Long> secondValues,
      Set<Long> thirdValues,
      ReportDeadlineDto report,
      List<Operation> ops) {
    report.setSecondRespValues(secondValues.stream()
        .map(eid -> {
          //поиск сотрудника, который соответствует очередному id из значений второго параметра,
          //дополнительно идет проверка, что сотрудник относится к операции из проекта из главного
          // блока отчета
          Employee e = ops.stream()
              .filter(o -> o.getEmployee() != null)
              .filter(o -> Objects.equals(o.getEmployee().getId(), eid)
                  && Objects.equals(o.getProject().getId(), firstValue))
              .findFirst()
              .orElseThrow(() -> new ReportException(HttpStatus.BAD_REQUEST,
                  "Employee with id: " + eid + NOT_FOUND_CONST.value + " in project with id: "
                      + firstValue))
              .getEmployee();

          return new SecondResponseSubDto(
              e.getId(), e.getLastName(),
              ops.stream()
                  .filter(o -> checkParticipatingEmployeeInOperation(e, o, thirdValues))
                  .map(o -> new ThirdResponseSubDto(o.getId(), o.getName(),
                      o.getPlannedEndDate(), o.getRealEndDate()))
                  .toList());
        }).toList());
  }

  private void addToPrReportSecondSubDtoByOperations(
      Long firstValue,
      Set<Long> secondValues,
      Set<Long> thirdValues,
      ReportDeadlineDto report,
      List<Operation> ops) {
    report.setSecondRespValues(secondValues.stream()
        .map(oid -> {
              //поиск операции, которая соответствует очередному id из значений второго параметра,
              //дополнительно идет проверка, что операция относится к проекту из главного
              // блока отчета
              Operation op = ops.stream()
                  .filter(o -> Objects.equals(o.getId(), oid)
                      && Objects.equals(o.getProject().getId(), firstValue))
                  .findFirst()
                  .orElseThrow(() -> new ReportException(HttpStatus.BAD_REQUEST,
                      OPERATION_WITH_ID.value + oid + NOT_FOUND_CONST.value
                          + " in project with id: " + firstValue));

              checkNotNullEmpInOp(op);
              //проверка на наличие id сотрудника из выборки операций среди значений
              // третьего параметра
              checkIdContainsInSetValues(thirdValues, op.getEmployee().getId());

              return new SecondResponseSubDto(op.getId(), op.getName(),
                  List.of(new ThirdResponseSubDto(
                      op.getEmployee().getId(),
                      op.getEmployee().getLastName(),
                      op.getPlannedEndDate(),
                      op.getRealEndDate())));
            }
        ).toList());
  }

  private void checkIdContainsInSetValues(Set<Long> values, Long id) {
    if (!values.contains(id)) {
      throw new ReportException(HttpStatus.BAD_REQUEST,
          "Values of parameter not contains this id: " + id);
    }
  }

  private void addToEmpReportSecondSubDtoByOperations(
      Long firstValue,
      Set<Long> secondValues,
      Set<Long> thirdValues,
      ReportDeadlineDto report,
      List<Operation> ops) {
    report.setSecondRespValues(secondValues.stream()
        .map(oid -> {
          //поиск операции, которая соответствует очередному id из значений второго параметра,
          //дополнительно идет проверка, что операция относится к сотруднику из главного
          // блока отчета
          Operation op = ops.stream()
              .filter(o -> Objects.equals(o.getId(), oid)
                  && Objects.equals(o.getEmployee().getId(), firstValue))
              .findFirst()
              .orElseThrow(() -> new ReportException(HttpStatus.BAD_REQUEST,
                  OPERATION_WITH_ID.value + oid + " and employee with id: "
                      + firstValue + NOT_FOUND_CONST.value
                      + ". Or not contains in values second parameter"));
          //поиск проекта, который соответствует найденной выше операции,
          //дополнительно идет проверка, что id проекта есть среди значений третьего параметра
          Project pr = ops.stream()
              .filter(o -> Objects.equals(o.getProject().getId(), op.getProject().getId())
                  && thirdValues.contains(o.getProject().getId()))
              .findFirst()
              .orElseThrow(() -> new ReportException(HttpStatus.BAD_REQUEST,
                  PROJECT_WITH_ID.value + op.getProject().getId() + " and operation with id: "
                      + op.getId() + NOT_FOUND_CONST.value
                      + ". Or not contains in values third parameter"))
              .getProject();
          return new SecondResponseSubDto(
              op.getId(), op.getName(),
              List.of(new ThirdResponseSubDto(pr.getId(), String.valueOf(pr.getNumber()),
                  op.getPlannedEndDate(), op.getRealEndDate())));
        })
        .toList());
  }

  private void addToEmpReportSecondSubDtoByProjects(
      Long firstValue,
      Set<Long> secondValues,
      Set<Long> thirdValues,
      ReportDeadlineDto report,
      List<Operation> ops) {
    report.setSecondRespValues(secondValues.stream()
        //поиск проекта, который соответствует очередному id из значений второго параметра,
        //дополнительно идет проверка, что проект относится к сотруднику из главного
        // блока отчета
        .map(pid -> {
          Project pr = ops.stream()
              .filter(o -> Objects.equals(o.getProject().getId(), pid)
                  && Objects.equals(o.getEmployee().getId(), firstValue))
              .findFirst()
              .orElseThrow(() -> new ReportException(HttpStatus.BAD_REQUEST,
                  PROJECT_WITH_ID.value + pid + " and employee with id: "
                      + firstValue + NOT_FOUND_CONST.value
                      + ". Or not contains in values second parameter"))
              .getProject();
          return new SecondResponseSubDto(
              pr.getId(), String.valueOf(pr.getNumber()), ops.stream()
              .filter(o -> Objects.equals(o.getProject().getId(), pid)
                  && thirdValues.contains(o.getId()))
              .map(o -> new ThirdResponseSubDto(
                  o.getId(), o.getName(), o.getPlannedEndDate(), o.getRealEndDate()))
              .toList());
        })
        .toList());
  }

  private void addToOpReportSecondSubDtoByProject(
      Set<Long> secondValues,
      Set<Long> thirdValues,
      ReportDeadlineDto report,
      Operation op) {

    checkNotNullEmpInOp(op);
    secondValues.forEach(p -> checkCorrectProjectIdFromReqAndOp(p, op));
    thirdValues.forEach(e -> checkCorrectEmpIdFromReqAndEmpIdFromOp(e, op));

    report.setSecondRespValues(
        List.of(new SecondResponseSubDto(
            op.getProject().getId(),
            String.valueOf(op.getProject().getNumber()),
            List.of(new ThirdResponseSubDto(
                op.getEmployee().getId(),
                op.getEmployee().getLastName(),
                op.getPlannedEndDate(),
                op.getRealEndDate())))));
  }

  private void addToOpReportSecondSubDtoByEmployee(
      Set<Long> secondValues,
      Set<Long> thirdValues,
      ReportDeadlineDto report,
      Operation op) {

    checkNotNullEmpInOp(op);
    secondValues.forEach(p -> checkCorrectProjectIdFromReqAndOp(p, op));
    thirdValues.forEach(e -> checkCorrectEmpIdFromReqAndEmpIdFromOp(e, op));

    report.setSecondRespValues(
        List.of(new SecondResponseSubDto(
            op.getEmployee().getId(),
            op.getEmployee().getLastName(),
            List.of(new ThirdResponseSubDto(
                op.getProject().getId(),
                String.valueOf(op.getProject().getNumber()),
                op.getPlannedEndDate(),
                op.getRealEndDate())))));
  }

  private void checkStartEndDates(LocalDate startOfPeriod, LocalDate endOfPeriod) {
    if (startOfPeriod.isAfter(endOfPeriod)) {
      throw new ReportException(HttpStatus.BAD_REQUEST, "Start date cannot be after end date.");
    }
  }

  private boolean checkParticipatingEmployeeInOperation(
      Employee e, Operation o, Set<Long> thirdValues) {
    return (o.getEmployee() != null
        && Objects.equals(o.getEmployee().getId(), e.getId())
        && thirdValues.contains(o.getId()));
  }
}
