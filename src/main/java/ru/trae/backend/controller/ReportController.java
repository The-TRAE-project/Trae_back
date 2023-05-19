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
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
      + "входящих в отчет",
      description = "Доступен администратору. Возвращает даты с началом и концом запрошенного "
          + "периода, два списка - с информацией по сотрудникам (id, имя и фамилию) и выборку по "
          + "рабочим сменам с процентным соотношением нахождения сотрудника на смене.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200",
          description = "Список отчетов по рабочим сменам за указанный период и список сотрудников,"
              + " входящих в отчет. В схеме указан единичный объект",
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
//      @RequestParam(name = "startOfPeriod")
//      @Parameter(description = "Начало периода запроса рабочих смен") LocalDate startOfPeriod,
//      @RequestParam(name = "endOfPeriod")
//      @Parameter(description = "Конец периода запроса рабочих смен") LocalDate endOfPeriod
  ) {
    return ResponseEntity.ok(
        //workingShiftService.getWorkingShiftEmployeePercentage(startOfPeriod, endOfPeriod));
        reportService.reportWorkingShiftForPeriod(
            LocalDate.now().minusDays(2), LocalDate.now().plusDays(2)));
  }
}
