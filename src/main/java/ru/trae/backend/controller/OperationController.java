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

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.trae.backend.dto.operation.InsertingOperationDto;
import ru.trae.backend.dto.operation.OperationForEmpDto;
import ru.trae.backend.dto.operation.OperationInWorkForEmpDto;
import ru.trae.backend.dto.operation.ReqOpEmpIdDto;
import ru.trae.backend.entity.task.Operation;
import ru.trae.backend.entity.task.Project;
import ru.trae.backend.service.OperationService;
import ru.trae.backend.service.ProjectService;

/**
 * Controller class that handles requests related to operations.
 *
 * @author Vladimir Olennikov
 */
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/operation")
public class OperationController {
  
  private final OperationService operationService;
  private final ProjectService projectService;
  
  /**
   * Inserts new {@link Operation} to the database.
   *
   * @param dto contains the data of the new {@link Operation}
   * @return {@link HttpStatus#OK} if the operation was added successfully
   */
  @io.swagger.v3.oas.annotations.Operation(summary = "Вставка операции в существующий проект",
      description = "Доступен администратору. Вставляет операцию в проект, возвращает статус 200.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200",
          description = "Возвращает статус 200 при успешной вставке операции",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = HttpStatus.class))}),
      @ApiResponse(responseCode = "400", description = "Неправильный формат идентификаторов. "
          + "Новая операция не может иметь приоритет ниже доступной для принятия.",
          content = @Content),
      @ApiResponse(responseCode = "401", description = "Требуется аутентификация",
          content = @Content),
      @ApiResponse(responseCode = "403", description = "Доступ запрещен",
          content = @Content),
      @ApiResponse(responseCode = "404",
          description = "Проект или тип работы с таким идентификатором не найдена",
          content = @Content),
      @ApiResponse(responseCode = "409",
          description = "Операция с таким приоритетом уже существует", content = @Content),
      @ApiResponse(responseCode = "423", description = "Учетная запись заблокирована",
          content = @Content)})
  @PostMapping("/insert")
  public ResponseEntity<HttpStatus> insertOperation(
      @Valid @RequestBody InsertingOperationDto dto) {
    Project p = projectService.getProjectById(dto.projectId());
    boolean shipmentIsAdded = operationService.insertNewOperation(dto, p);
    projectService.updatePlannedEndDateAfterInsertDeleteOp(p, true, shipmentIsAdded);
    
    return ResponseEntity.ok().build();
  }
  
  /**
   * Delete operation by id.
   *
   * @param operationId the operation id
   * @return the response entity
   */
  @io.swagger.v3.oas.annotations.Operation(summary = "Удаление операции",
      description = "Доступен администратору. Удаляет операцию, возвращает статус 204.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204",
          description = "Возвращает статус 204 при успешном удалении операции",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = HttpStatus.class))}),
      @ApiResponse(responseCode = "400", description = "Неправильный формат идентификатора",
          content = @Content),
      @ApiResponse(responseCode = "401", description = "Требуется аутентификация",
          content = @Content),
      @ApiResponse(responseCode = "403", description = "Доступ запрещен",
          content = @Content),
      @ApiResponse(responseCode = "404",
          description = "Операция с таким идентификатором не найдена", content = @Content),
      @ApiResponse(responseCode = "423", description = "Учетная запись заблокирована",
          content = @Content)})
  @DeleteMapping("/delete-operation/{operationId}")
  public ResponseEntity<HttpStatus> deleteOperation(@PathVariable long operationId) {
    operationService.deleteOperation(operationId);
    projectService.updatePlannedEndDateAfterInsertDeleteOp(
        projectService.getProjectByOperationId(operationId), false, false);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
  
  /**
   * Close an existing operation.
   *
   * @param operationId id of the operation to close.
   * @return OK if the operation has been closed.
   */
  @io.swagger.v3.oas.annotations.Operation(summary = "Закрытие операции",
      description = "Доступен администратору. Закрывает операцию, возвращает статус 200.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200",
          description = "Возвращает статус 200 при успешном закрытии операции",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = HttpStatus.class))}),
      @ApiResponse(responseCode = "400", description = "Неправильный формат идентификатора или "
          + "операция еще/уже не доступна для принятия и не может быть закрыта",
          content = @Content),
      @ApiResponse(responseCode = "401", description = "Требуется аутентификация",
          content = @Content),
      @ApiResponse(responseCode = "403", description = "Доступ запрещен",
          content = @Content),
      @ApiResponse(responseCode = "404",
          description = "Операция с таким идентификатором не найдена", content = @Content),
      @ApiResponse(responseCode = "409", description = "Операция уже закрыта",
          content = @Content),
      @ApiResponse(responseCode = "423", description = "Учетная запись заблокирована",
          content = @Content)})
  @PostMapping("/close")
  public ResponseEntity<HttpStatus> closeOperation(
      @RequestParam(value = "operationId") long operationId) {
    operationService.closeOperation(operationId);
    return ResponseEntity.ok().build();
  }
  
  /**
   * Retrieves the list of operations associated with a given project id.
   *
   * @param projectId the id of the project to retrieve operations for
   * @return the list of operations associated with the given project
   */
  @io.swagger.v3.oas.annotations.Operation(summary = "Список операций проекта для сотрудников",
      description = "Доступен сотрудникам. Возвращает список операций, по указанному ID проекта")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Список операций выбранного проекта. "
          + "В примере указан единичный объект из списка",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = OperationForEmpDto.class))}),
      @ApiResponse(responseCode = "400", description = "Неправильный формат идентификатора",
          content = @Content),
      @ApiResponse(responseCode = "401", description = "Требуется аутентификация",
          content = @Content),
      @ApiResponse(responseCode = "403", description = "Доступ запрещен",
          content = @Content),
      @ApiResponse(responseCode = "423", description = "Учетная запись заблокирована",
          content = @Content)})
  @GetMapping("/employee/project-operations/{projectId}")
  public ResponseEntity<List<OperationForEmpDto>> operationsByProjectId(
      @PathVariable long projectId) {
    return ResponseEntity.ok(operationService.getOperationsByProjectIdForEmp(projectId));
  }
  
  /**
   * Get all operations in work for specified employee.
   *
   * @param employeeId id of employee
   * @return list of operations in work for specified employee
   */
  @io.swagger.v3.oas.annotations.Operation(
      summary = "Список операций принятых в работу сотрудником",
      description = "Доступен сотрудникам. Возвращает список операций принятых в работу "
          + "сотрудником по его ID")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200",
          description = "Список операций принятых в работу указанным сотрудником"
              + "В примере указан единичный объект из списка",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = OperationInWorkForEmpDto.class))}),
      @ApiResponse(responseCode = "400", description = "Неправильный формат идентификатора",
          content = @Content),
      @ApiResponse(responseCode = "401", description = "Требуется аутентификация",
          content = @Content),
      @ApiResponse(responseCode = "403", description = "Доступ запрещен",
          content = @Content),
      @ApiResponse(responseCode = "423", description = "Учетная запись заблокирована",
          content = @Content)})
  @GetMapping("/employee/operations-in-work/{employeeId}")
  public ResponseEntity<List<OperationInWorkForEmpDto>> operationsInWorkByEmpId(
      @PathVariable long employeeId) {
    return ResponseEntity.ok(operationService.getOperationsInWorkByEmpIdForEmp(employeeId));
  }
  
  /**
   * Endpoint to receive operation.
   *
   * @param dto The request body of type {@link ReqOpEmpIdDto}
   * @return {@link ResponseEntity} HttpStatus.OK
   */
  @io.swagger.v3.oas.annotations.Operation(
      summary = "Принятие операции сотрудником", description = "Доступен сотрудникам. "
      + "Закрепляет выбранную операцию за сотрудником, возвращает статус 200")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200",
          description = "Возвращает статус 200 при успешном принятии операции",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = HttpStatus.class))}),
      @ApiResponse(responseCode = "400",
          description = "Неправильный формат идентификаторов (операции и/или сотрудника). "
              + "Операция не доступна для принятия. "
              + "Работник не имеет требуемой квалификации(типа работы)",
          content = @Content),
      @ApiResponse(responseCode = "401", description = "Требуется аутентификация",
          content = @Content),
      @ApiResponse(responseCode = "403", description = "Доступ запрещен",
          content = @Content),
      @ApiResponse(responseCode = "404",
          description = "Операция и/или сотрудник с таким идентификатором не найдена",
          content = @Content),
      @ApiResponse(responseCode = "423", description = "Учетная запись заблокирована",
          content = @Content)})
  @PostMapping("/employee/receive-operation")
  public ResponseEntity<HttpStatus> receiveOperation(@Valid @RequestBody ReqOpEmpIdDto dto) {
    operationService.checkCorrectIdAndPriority(dto.operationId(), dto.operationPriority());
    operationService.receiveOperation(dto);
    if (dto.operationPriority() == 0) {
      projectService.updateStartFirstOperationDate(dto.operationId());
    }
    return ResponseEntity.ok().build();
  }
  
  /**
   * The method is used to finish an operation and update the project end date if needed.
   *
   * @param dto The request body of type {@link ReqOpEmpIdDto}
   * @return {@link ResponseEntity} HttpStatus.OK
   */
  @io.swagger.v3.oas.annotations.Operation(
      summary = "Закрытие операции сотрудником", description = "Доступен сотрудникам. "
      + "Закрывает выбранную операцию, возвращает статус 200")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200",
          description = "Возвращает статус 200 при успешном закрытии операции",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = HttpStatus.class))}),
      @ApiResponse(responseCode = "400",
          description = "Неправильный формат идентификаторов (операции и/или сотрудника). "
              + "ID принявшего сотрудника и указанного в запросе - не совпадают",
          content = @Content),
      @ApiResponse(responseCode = "401", description = "Требуется аутентификация",
          content = @Content),
      @ApiResponse(responseCode = "403", description = "Доступ запрещен",
          content = @Content),
      @ApiResponse(responseCode = "404",
          description = "Операция и/или сотрудник с таким идентификатором не найдена",
          content = @Content),
      @ApiResponse(responseCode = "423", description = "Учетная запись заблокирована",
          content = @Content)})
  @PostMapping("/employee/finish-operation")
  public ResponseEntity<HttpStatus> finishOperation(@Valid @RequestBody ReqOpEmpIdDto dto) {
    Operation o = operationService.getOperationById(dto.operationId());
    
    operationService.checkIfOpAlreadyFinishedOrClosed(o);
    operationService.checkConfirmingEmployee(o, dto.employeeId());
    operationService.finishOperation(o);
    projectService.checkAndUpdateProjectEndDateAfterFinishOperation(o);
    
    return ResponseEntity.ok().build();
  }
}
