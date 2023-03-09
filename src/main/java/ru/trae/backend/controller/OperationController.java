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

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.trae.backend.dto.operation.InsertingOperationDto;
import ru.trae.backend.dto.operation.OperationDto;
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
   * This method provides a list of short operations by project.
   *
   * @param projectId the project id
   * @return the list of operation dto
   */
  @GetMapping("/project-operations/{projectId}")
  public ResponseEntity<List<OperationDto>> shortOperationsByProject(@PathVariable long projectId) {
    return ResponseEntity.ok(operationService.getOpsDtoListByProject(projectId));
  }

  @PostMapping("/insert-without-closing-active")
  public ResponseEntity<HttpStatus> insertOperationWithoutCloseActive(
          @RequestBody InsertingOperationDto dto) {
    Project p = projectService.getProjectById(dto.projectId());
    operationService.insertNewOperationWithoutCloseActive(dto, p);

    return ResponseEntity.ok().build();
  }

  /**
   * Retrieves the list of operations associated with a given project id.
   *
   * @param projectId the id of the project to retrieve operations for
   * @return the list of operations associated with the given project
   */
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
  @PatchMapping("/employee/receive-operation")
  public ResponseEntity<HttpStatus> receiveOperation(@RequestBody ReqOpEmpIdDto dto) {
    operationService.receiveOperation(dto);
    return ResponseEntity.ok().build();
  }

  /**
   * The method is used to finish an operation and update the project end date if needed.
   *
   * @param dto The request body of type {@link ReqOpEmpIdDto}
   * @return {@link ResponseEntity} HttpStatus.OK
   */
  @PatchMapping("/employee/finish-operation")
  public ResponseEntity<HttpStatus> finishOperation(@RequestBody ReqOpEmpIdDto dto) {
    Operation o = operationService.getOperationById(dto.operationId());

    operationService.checkConfirmingEmployee(o, dto.employeeId());
    projectService.checkAndUpdateProjectEndDate(o);
    operationService.finishOperation(o);
    return ResponseEntity.ok().build();
  }
}
