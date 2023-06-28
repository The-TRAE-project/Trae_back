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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import ru.trae.backend.dto.operation.NewOperationDto;
import ru.trae.backend.dto.operation.ReceiveOpReq;
import ru.trae.backend.entity.TypeWork;
import ru.trae.backend.entity.task.Operation;
import ru.trae.backend.entity.task.Project;
import ru.trae.backend.entity.user.Employee;
import ru.trae.backend.exceptionhandler.exception.OperationException;
import ru.trae.backend.factory.OperationFactory;
import ru.trae.backend.repository.OperationRepository;

@ExtendWith(MockitoExtension.class)
class OperationServiceTest {
  @Mock
  private OperationRepository operationRepository;
  @Mock
  private OperationFactory operationFactory;
  @Mock
  private EmployeeService employeeService;
  @InjectMocks
  private OperationService operationService;
  private Employee e;
  private Project p;
  private Operation o;
  private LocalDateTime plannedEndDate = LocalDateTime.now().plusHours(60);
  private Long operationId = 1L;
  private int opPeriod = 60;
  private int opPriority = 0;
  private Long typeWorkId = 1L;
  private Long employeeId = 1L;
  private String opName = "test_operation_name";
  
  @BeforeEach
  void setUp() {
    p = new Project();
    p.setId(1L);
    p.setEnded(false);
    
    TypeWork tw = new TypeWork();
    tw.setId(typeWorkId);
    tw.setActive(true);
    
    o = new Operation();
    o.setId(operationId);
    o.setPeriod(opPeriod);
    o.setPriority(opPriority);
    o.setName(opName);
    o.setTypeWork(tw);
    o.setPlannedEndDate(plannedEndDate);
    
    e = new Employee();
    e.setActive(true);
    e.setId(employeeId);
    e.setTypeWorks(Set.of(tw));
  }
  
  @Test
  void getOperationById_ExistingId_ReturnsOperation() {
    //when
    when(operationRepository.findById(operationId)).thenReturn(Optional.of(o));
    
    Operation actualOperation = operationService.getOperationById(operationId);
    
    //then
    assertEquals(o, actualOperation);
    verify(operationRepository, times(1)).findById(operationId);
  }
  
  @Test
  void getOperationById_NonExistingId_ThrowsOperationException() {
    //when
    when(operationRepository.findById(operationId)).thenReturn(Optional.empty());
    
    OperationException exception = assertThrows(OperationException.class,
        () -> operationService.getOperationById(operationId));
    
    //then
    assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    assertEquals("Operation with ID " + operationId + " not found", exception.getMessage());
    verify(operationRepository, times(1)).findById(operationId);
  }
  
  @Test
  void saveNewOperations_EmptyOperationsList_ShouldNotCreateOperations() {
    //given
    List<NewOperationDto> operations = new ArrayList<>();
    
    //when
    operationService.saveNewOperations(p, operations);
    
    //then
    verify(operationRepository, Mockito.times(0)).save(any(Operation.class));
  }
  
  @Test
  void saveNewOperations_NullOperationsList_ShouldNotCreateOperations() {
    //when
    operationService.saveNewOperations(p, null);
    
    //then
    verify(operationRepository, Mockito.times(0)).save(any(Operation.class));
  }
  
  @Test
  void saveNewOperations_MultipleOperations_ShouldCreateOperations() {
    //given
    List<NewOperationDto> operations = new ArrayList<>();
    NewOperationDto operationDto1 = new NewOperationDto(opName, typeWorkId);
    NewOperationDto operationDto2 = new NewOperationDto("test_operation_name2", typeWorkId);
    operations.add(operationDto1);
    operations.add(operationDto2);
    
    //when
    when(operationFactory.create(eq(p), eq(opName), anyInt(), anyInt(), any(LocalDateTime.class), anyBoolean(), eq(typeWorkId))).thenReturn(o);
    when(operationFactory.create(eq(p), eq("test_operation_name2"), anyInt(), anyInt(), isNull(), anyBoolean(), eq(typeWorkId))).thenReturn(o);
    
    operationService.saveNewOperations(p, operations);
    
    //then
    verify(operationRepository, times(2)).save(any(Operation.class));
  }
  
  @Test
  void saveNewOperations_OneOperation_ShouldCreateOperations() {
    //given
    List<NewOperationDto> operations = new ArrayList<>();
    NewOperationDto operationDto1 = new NewOperationDto(opName, typeWorkId);
    operations.add(operationDto1);
    
    //when
    when(operationFactory.create(eq(p), eq(opName), anyInt(), anyInt(), any(LocalDateTime.class), anyBoolean(), eq(typeWorkId))).thenReturn(o);
    
    operationService.saveNewOperations(p, operations);
    
    //then
    verify(operationRepository, times(1)).save(any(Operation.class));
  }
  
  @Test
  void receiveOperation_ShouldSetOperationPropertiesAndSave() {
    //given
    o.setReadyToAcceptance(true);
    ReceiveOpReq dto = new ReceiveOpReq(employeeId, opPriority, operationId);
    when(employeeService.getEmployeeById(employeeId)).thenReturn(e);
    
    when(operationRepository.findById(operationId)).thenReturn(Optional.ofNullable(o));
    
    operationService.receiveOperation(dto);
    
    //then
    assertTrue(o.isInWork());
    assertFalse(o.isReadyToAcceptance());
    assertEquals(e, o.getEmployee());
    assertNotNull(o.getAcceptanceDate());
    verify(operationRepository).save(o);
  }
  
}
