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
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.trae.backend.dto.WorkingShiftDto;
import ru.trae.backend.service.WorkingShiftService;

/**
 * WorkingShiftController is used to provide endpoints to handle requests related to WorkingShift
 * objects.
 *
 * @author Vladimir Olennikov
 */
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/working-shift")
public class WorkingShiftController {
  private final WorkingShiftService workingShiftService;

  @GetMapping("/active")
  public ResponseEntity<WorkingShiftDto> activeWorkingShift() {
    return ResponseEntity.ok(workingShiftService.getActive());
  }

  @Operation(summary = "Проверка нахождения сотрудника на смене",
      description = "Доступен сотрудникам. Проверяет, находится ли сотрудник на смене. "
          + "Если да, то возвращает true, если нет, то - false.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200",
          description = "Возвращает булеан значения (true или false).",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = Boolean.class))}),
      @ApiResponse(responseCode = "400", description = "Неправильный формат идентификатора",
          content = @Content),
      @ApiResponse(responseCode = "401", description = "Требуется аутентификация",
          content = @Content),
      @ApiResponse(responseCode = "403", description = "Доступ запрещен",
          content = @Content),
      @ApiResponse(responseCode = "404",
          description = "Сотрудник с таким идентификатором не найден", content = @Content),
      @ApiResponse(responseCode = "423", description = "Учетная запись заблокирована",
          content = @Content)})
  @GetMapping("/on-shift/{employeeId}")
  public ResponseEntity<Boolean> statusEmployee(@PathVariable long employeeId) {
    return ResponseEntity.ok(workingShiftService.employeeOnShift(true, employeeId));
  }
}
