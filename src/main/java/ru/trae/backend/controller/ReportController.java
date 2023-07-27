/*
 * Copyright (c) 2023. Vladimir Olennikov.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.trae.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.time.LocalDate;
import java.util.Set;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.trae.backend.dto.report.DeadlineReq;
import ru.trae.backend.dto.report.ReportDashboardStatsDto;
import ru.trae.backend.dto.report.ReportDeadlineDto;
import ru.trae.backend.dto.report.ReportProjectsForPeriodDto;
import ru.trae.backend.dto.report.ReportWorkingShiftForPeriodDto;
import ru.trae.backend.service.ReportService;

/**
 * ReportController is used to provide endpoints to handle requests related to Report
 * objects.
 *
 * @author Vladimir Olennikov
 */
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/report")
public class ReportController {
  private final ReportService reportService;

  @Operation(summary = "Список отчетов по рабочим сменам за указанный период, список сотрудников, "
      + "входящих в отчет, список общего количества часов по каждому сотруднику",
      description = "Доступен администратору. Возвращает даты с началом и концом запрошенного "
          + "периода, три списка - с информацией по сотрудникам (id, имя и фамилию), выборку по "
          + "рабочим сменам с часами нахождения сотрудника на смене и данные с общим количеством "
          + "часов по каждому сотруднику в указанный период.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200",
          description = "Список отчетов по рабочим сменам за указанный период и список сотрудников,"
              + " входящих в отчет, список общего количества часов по каждому сотруднику.",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = ReportWorkingShiftForPeriodDto.class))}),
      @ApiResponse(responseCode = "400",
          description = "Неправильный формат даты начала и/или конца периода",
          content = @Content),
      @ApiResponse(responseCode = "401", description = "Требуется аутентификация",
          content = @Content),
      @ApiResponse(responseCode = "403", description = "Доступ запрещен",
          content = @Content),
      @ApiResponse(responseCode = "404",
          description = "Сотрудник с таким идентификатором не найден", content = @Content),
      @ApiResponse(responseCode = "423", description = "Учетная запись заблокирована",
          content = @Content)})
  @GetMapping("/working-shifts-for-period")
  public ResponseEntity<ReportWorkingShiftForPeriodDto> workingShiftsForPeriod(
      @RequestParam(name = "startOfPeriod") @DateTimeFormat(pattern = "yyyy-MM-dd")
      @Parameter(description = "Начало периода запроса рабочих смен") LocalDate startOfPeriod,
      @RequestParam(name = "endOfPeriod") @DateTimeFormat(pattern = "yyyy-MM-dd")
      @Parameter(description = "Конец периода запроса рабочих смен") LocalDate endOfPeriod,
      @RequestParam(name = "employeeIds", required = false)
      @Parameter(description = "Список ID конкретных сотрудников") Set<Long> employeeIds
  ) {
    return ResponseEntity.ok(reportService.reportWorkingShiftForPeriod(
        startOfPeriod, endOfPeriod, employeeIds));
  }


  @Operation(summary = "Отчет со списком проектов попадающих в указанный период",
      description = "Доступен администратору. Возвращает даты с началом и концом запрошенного "
          + "периода, дату формирования отчета, выборку проектов попадающих в указанный период. "
          + "В случае, если проект не завершен, то операциям без дат начала и конца рассчитывается "
          + "планируемые даты начала и конца выполнения таким методом: дата окончания предыдущей "
          + "операции служит датой начала следующей, а дата окончания следующей = дата начала "
          + "следующей + период выполнения операций(указан в проекте).")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200",
          description = "Отчет со списком проектов попадающих в указанный период",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = ReportProjectsForPeriodDto.class))}),
      @ApiResponse(responseCode = "400",
          description = "Неправильный формат даты начала и/или конца периода",
          content = @Content),
      @ApiResponse(responseCode = "401", description = "Требуется аутентификация",
          content = @Content),
      @ApiResponse(responseCode = "403", description = "Доступ запрещен",
          content = @Content),
      @ApiResponse(responseCode = "404",
          description = "Сотрудник с таким идентификатором не найден", content = @Content),
      @ApiResponse(responseCode = "423", description = "Учетная запись заблокирована",
          content = @Content)})
  @GetMapping("/projects-for-period")
  public ResponseEntity<ReportProjectsForPeriodDto> projectsForPeriod(
      @RequestParam(name = "startOfPeriod") @DateTimeFormat(pattern = "yyyy-MM-dd")
      @Parameter(description = "Начало периода запроса информации по проектам")
      LocalDate startOfPeriod,
      @RequestParam(name = "endOfPeriod") @DateTimeFormat(pattern = "yyyy-MM-dd")
      @Parameter(description = "Конец периода запроса информации по проектам") LocalDate endOfPeriod
  ) {
    return ResponseEntity.ok(reportService.reportProjectsForPeriod(startOfPeriod, endOfPeriod));
  }

  @Operation(summary = "Отчет по срокам по трем параметрам (сотрудники, операции, проекты)",
      description = """
          Доступен администратору. Возвращает выборку по трем параметрам. Для работы с эндпоинтом\s
          обязательно заполнять все 3 параметра и их значения. Важно: в полях firstParameter,\s
          secondParameter, thirdParameter - должны быть указаны названия параметров. Названия\s
          параметров строго определены: PROJECT, OPERATION, EMPLOYEE и не должны повторяться в\s
          рамках одного запроса.
          """)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200",
          description = "Отчет по срокам по трем параметрам (сотрудники, операции, проекты)",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = ReportDeadlineDto.class))}),
      @ApiResponse(responseCode = "400",
          description = "Отсутствует один или несколько обязательных параметров в теле запроса. "
              + "Неправильный формат параметра в теле запроса",
          content = @Content),
      @ApiResponse(responseCode = "401", description = "Требуется аутентификация",
          content = @Content),
      @ApiResponse(responseCode = "403", description = "Доступ запрещен",
          content = @Content),
      @ApiResponse(responseCode = "404",
          description = "Сотрудник с таким идентификатором не найден", content = @Content),
      @ApiResponse(responseCode = "409",
          description = "В запросе присутствуют повторяющиеся значения параметров",
          content = @Content),
      @ApiResponse(responseCode = "423", description = "Учетная запись заблокирована",
          content = @Content)})
  @GetMapping("/deadlines")
  public ResponseEntity<ReportDeadlineDto> deadlines(@Valid @RequestBody DeadlineReq req) {
    return ResponseEntity.ok(reportService.reportDeadlines(req));
  }

  /**
   * Retrieves the dashboard statistics for the report.
   *
   * @return A ResponseEntity containing the {@link ReportDashboardStatsDto} object with the
   *     dashboard statistics.
   */
  @io.swagger.v3.oas.annotations.Operation(
      summary = "Статистика для дашборда",
      description = "Доступен администратору. Возвращает ДТО с количеством сотрудников на активной "
          + "рабочей смене, незавершенных проектов, проектов с просроченной датой по договору, "
          + "проектов с текущим этапом у которого просрочено временем выполнения, проектов с "
          + "последней операцией в статусе \"готова для принятия\"")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200",
          description = "ДТО со статистикой для дашборда",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = ReportDashboardStatsDto.class))}),
      @ApiResponse(responseCode = "400", description = "Неправильный формат идентификатора",
          content = @Content),
      @ApiResponse(responseCode = "401", description = "Требуется аутентификация",
          content = @Content),
      @ApiResponse(responseCode = "403", description = "Доступ запрещен",
          content = @Content),
      @ApiResponse(responseCode = "423", description = "Учетная запись заблокирована",
          content = @Content)})
  @GetMapping("/dashboard")
  public ResponseEntity<ReportDashboardStatsDto> dashboardStats() {
    return ResponseEntity.ok(reportService.getDashboardStatsDto());
  }
}
