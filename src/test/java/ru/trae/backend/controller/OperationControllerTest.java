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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.trae.backend.dto.operation.FinishOpReq;
import ru.trae.backend.dto.operation.InsertingOperationDto;
import ru.trae.backend.dto.operation.OperationForEmpDto;
import ru.trae.backend.dto.operation.OperationInWorkForEmpDto;
import ru.trae.backend.dto.operation.ReceiveOpReq;
import ru.trae.backend.entity.task.Operation;
import ru.trae.backend.entity.task.Project;
import ru.trae.backend.projection.OperationIdNameProjectNumberDto;
import ru.trae.backend.service.OperationService;
import ru.trae.backend.service.ProjectService;

@ExtendWith(MockitoExtension.class)
class OperationControllerTest {
  @Mock
  private OperationService operationService;
  @Mock
  private ProjectService projectService;
  @InjectMocks
  private OperationController operationController;
  private final long projectId = 1;
  private final long operationId = 1;
  private final String operationName = "test_operation_name";
  
  @Test
  void insertOperation_WhenValidDto_ShouldReturnHttpStatusOk() {
    //given
    InsertingOperationDto dto = new InsertingOperationDto(projectId, operationName, 1L, 10);
    boolean shipmentIsAdded = true;
    Project project = new Project();
    
    //when
    when(projectService.getProjectById(dto.projectId())).thenReturn(project);
    when(operationService.insertNewOperation(dto, project)).thenReturn(shipmentIsAdded);
    
    ResponseEntity<HttpStatus> response = operationController.insertOperation(dto);
    
    //then
    assertEquals(HttpStatus.OK, response.getStatusCode());
    verify(projectService).updatePlannedEndDateAfterInsertDeleteOp(project, true, shipmentIsAdded);
  }
  
  
  @Test
  void deleteOperation_WhenValidOperationId_ShouldReturnHttpStatusNoContent() {
    //given
    Project project = new Project();
    
    //when
    when(projectService.getProjectByOperationId(operationId)).thenReturn(project);
    
    ResponseEntity<HttpStatus> response = operationController.deleteOperation(operationId);
    
    //then
    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    verify(operationService).deleteOperation(operationId);
    verify(projectService).updatePlannedEndDateAfterInsertDeleteOp(project, false, false);
  }
  
  @Test
  void closeOperation_WhenValidOperationId_ShouldReturnHttpStatusOk() {
    //given
    Operation operation = new Operation();
    when(operationService.getOperationById(operationId)).thenReturn(operation);
    
    //when
    ResponseEntity<HttpStatus> response = operationController.closeOperation(operationId);
    
    //then
    assertEquals(HttpStatus.OK, response.getStatusCode());
    verify(operationService).checkIfOpAlreadyFinishedOrClosed(operation);
    verify(operationService).closeOperation(operation);
    verify(projectService).checkAndUpdateProjectEndDateAfterFinishOperation(operation);
  }
  
  @Test
  void operationForReportWithoutPagination_WhenValidParameters_ShouldReturnListOfOperationIdNameProjectNumberDto() {
    //given
    LocalDate startOfPeriod = LocalDate.parse("2023-01-01");
    LocalDate endOfPeriod = LocalDate.parse("2023-06-30");
    Set<Long> projectIds = new HashSet<>(List.of(1L, 2L));
    Set<Long> employeeIds = new HashSet<>(List.of(100L, 200L));
    
    List<OperationIdNameProjectNumberDto> expectedResult = Collections.emptyList();
    
    //when
    when(operationService.getOperationIdNameProjectNumberDtoList(
        projectIds, employeeIds, startOfPeriod, endOfPeriod))
        .thenReturn(expectedResult);
    
    ResponseEntity<List<OperationIdNameProjectNumberDto>> response =
        operationController.operationForReportWithoutPagination(startOfPeriod, endOfPeriod,
            projectIds, employeeIds);
    
    //then
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(expectedResult, response.getBody());
    verify(operationService).getOperationIdNameProjectNumberDtoList(
        projectIds, employeeIds, startOfPeriod, endOfPeriod);
  }
  
  @Test
  void operationsByProjectId_WhenValidProjectId_ShouldReturnListOfOperationForEmpDto() {
    //given
    List<OperationForEmpDto> expectedResult = List.of(
        new OperationForEmpDto(operationId, 10, operationName, true,
            false, false, "emp_first_name", "emp_last_name")
    );
    
    //when
    when(operationService.getOperationsByProjectIdForEmp(projectId))
        .thenReturn(expectedResult);
    
    ResponseEntity<List<OperationForEmpDto>> response =
        operationController.operationsByProjectId(projectId);
    
    //then
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(expectedResult, response.getBody());
    verify(operationService).getOperationsByProjectIdForEmp(projectId);
  }
  
  @Test
  void operationsInWorkByEmpId_WhenValidEmployeeId_ShouldReturnListOfOperationInWorkForEmpDto() {
    //given
    long employeeId = 1L;
    
    List<OperationInWorkForEmpDto> expectedResult = List.of(
        new OperationInWorkForEmpDto(operationId, projectId, 100,
            "test_project_name", operationName, "customer"));
    
    //when
    when(operationService.getOperationsInWorkByEmpIdForEmp(employeeId))
        .thenReturn(expectedResult);
    
    ResponseEntity<List<OperationInWorkForEmpDto>> response =
        operationController.operationsInWorkByEmpId(employeeId);
    
    //then
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(expectedResult, response.getBody());
    verify(operationService).getOperationsInWorkByEmpIdForEmp(employeeId);
  }
  
  @Test
  void receiveOperation_WhenValidRequest_ShouldReturnHttpStatusOk() {
    //given
    ReceiveOpReq request = new ReceiveOpReq(operationId, 10, 1L);
    
    //when
    ResponseEntity<HttpStatus> response = operationController.receiveOperation(request);
    
    //then
    assertEquals(HttpStatus.OK, response.getStatusCode());
    verify(operationService).checkCorrectIdAndPriority(request.operationId(), request.operationPriority());
    verify(operationService).receiveOperation(request);
  }
  
  @Test
  void receiveOperation_WhenValidRequestAndPriority0_ShouldReturnHttpStatusOk() {
    //given
    ReceiveOpReq request = new ReceiveOpReq(operationId, 0, 1L);
    
    //when
    ResponseEntity<HttpStatus> response = operationController.receiveOperation(request);
    
    //then
    assertEquals(HttpStatus.OK, response.getStatusCode());
    verify(operationService).checkCorrectIdAndPriority(request.operationId(), request.operationPriority());
    verify(operationService).receiveOperation(request);
    verify(projectService).updateStartFirstOperationDate(request.operationId());
  }
  
  @Test
  void finishOperation_WhenValidRequest_ShouldReturnHttpStatusOk() {
    //given
    FinishOpReq request = new FinishOpReq(operationId, 1L);

    Operation operation = new Operation();
    operation.setId(request.operationId());
    
    //when
    when(operationService.getOperationById(request.operationId())).thenReturn(operation);
    
    ResponseEntity<HttpStatus> response = operationController.finishOperation(request);
    
    //then
    assertEquals(HttpStatus.OK, response.getStatusCode());
    verify(operationService).checkIfOpAlreadyFinishedOrClosed(operation);
    verify(operationService).checkConfirmingEmployee(operation, request.employeeId());
    verify(operationService).finishOperation(operation);
    verify(projectService).checkAndUpdateProjectEndDateAfterFinishOperation(operation);
  }
}
