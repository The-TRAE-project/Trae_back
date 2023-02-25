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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.trae.backend.dto.operation.OperationDto;
import ru.trae.backend.dto.operation.OperationForEmpDto;
import ru.trae.backend.dto.operation.OperationInWorkForEmpDto;
import ru.trae.backend.dto.operation.ReqOpEmpIdDto;
import ru.trae.backend.dto.operation.WrapperNewOperationDto;
import ru.trae.backend.service.OperationService;

/**
 * Controller class that handles requests related to operations.
 *
 * @author Vladimir Olennikov
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/operation")
public class OperationController {

  private final OperationService operationService;

  @PostMapping("/new")
  public ResponseEntity operationPersist(@RequestBody WrapperNewOperationDto wrapper) {
    operationService.saveNewOperations(wrapper);
    return ResponseEntity.ok().build();
  }

  @GetMapping("/project-operations/{projectId}")
  public ResponseEntity<List<OperationDto>> shortOperationsByProject(@PathVariable long projectId) {
    return ResponseEntity.ok(operationService.getOpsDtoListByProject(projectId));
  }

  @GetMapping("/employee/project-operations/{projectId}")
  public ResponseEntity<List<OperationForEmpDto>> operationsByProjectId(
          @PathVariable long projectId) {
    return ResponseEntity.ok(operationService.getOperationsByProjectIdForEmp(projectId));
  }

  @GetMapping("/employee/operations-in-work/{employeeId}")
  public ResponseEntity<List<OperationInWorkForEmpDto>> operationsInWorkByEmpId(
          @PathVariable long employeeId) {
    return ResponseEntity.ok(operationService.getOperationsInWorkByEmpIdForEmp(employeeId));
  }

  @PostMapping("/employee/receive-operation")
  public ResponseEntity receiveOperation(@RequestBody ReqOpEmpIdDto dto) {
    operationService.receiveOperation(dto);
    return ResponseEntity.ok().build();
  }

  @PostMapping("/employee/finish-operation")
  public ResponseEntity finishOperation(@RequestBody ReqOpEmpIdDto dto) {
    operationService.finishOperation(dto);
    return ResponseEntity.ok().build();
  }
}
