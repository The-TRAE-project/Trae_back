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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
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
import ru.trae.backend.dto.operation.InsertingOperationDto;
import ru.trae.backend.dto.operation.NewOperationDto;
import ru.trae.backend.dto.operation.OperationForEmpDto;
import ru.trae.backend.dto.operation.OperationInWorkForEmpDto;
import ru.trae.backend.dto.operation.ReceiveOpReq;
import ru.trae.backend.entity.TypeWork;
import ru.trae.backend.entity.task.Operation;
import ru.trae.backend.entity.task.Project;
import ru.trae.backend.entity.user.Employee;
import ru.trae.backend.exceptionhandler.exception.OperationException;
import ru.trae.backend.exceptionhandler.exception.ProjectException;
import ru.trae.backend.factory.OperationFactory;
import ru.trae.backend.projection.OperationIdNameProjectNumberDto;
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
    o.setProject(p);
    
    e = new Employee();
    e.setActive(true);
    e.setId(employeeId);
    e.setTypeWorks(Set.of(tw));
    
    p.setOperations(List.of(o));
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
    assertEquals("Operation with id: " + operationId + " not found", exception.getMessage());
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
    
    //when
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
  
  @Test
  void receiveOperation_ShouldThrowExceptionCurrentOpNotAcceptance() {
    //given
    o.setReadyToAcceptance(false);
    ReceiveOpReq dto = new ReceiveOpReq(employeeId, opPriority, operationId);
    
    //when
    when(employeeService.getEmployeeById(employeeId)).thenReturn(e);
    when(operationRepository.findById(operationId)).thenReturn(Optional.ofNullable(o));
    
    OperationException exception = assertThrows(OperationException.class,
        () -> operationService.receiveOperation(dto));
    
    //then
    assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    assertEquals("The operation is currently unavailable for acceptance.", exception.getMessage());
  }
  
  @Test
  void receiveOperation_ShouldThrowExceptionTypeWorkAreNotCompatible() {
    //given
    TypeWork typeWork = new TypeWork();
    typeWork.setId(2L);
    o.setReadyToAcceptance(true);
    o.setTypeWork(typeWork);
    ReceiveOpReq dto = new ReceiveOpReq(employeeId, opPriority, operationId);
    
    //when
    when(employeeService.getEmployeeById(employeeId)).thenReturn(e);
    when(operationRepository.findById(operationId)).thenReturn(Optional.ofNullable(o));
    
    OperationException exception = assertThrows(OperationException.class,
        () -> operationService.receiveOperation(dto));
    
    //then
    assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    assertEquals("Types of work are not compatible.", exception.getMessage());
  }
  
  @Test
  void finishOperation_ShouldSetOperationPropertiesAndSave() {
    //given
    o.setEmployee(e);
    o.setInWork(true);
    o.setEnded(false);
    
    LocalDateTime previousEndDate = o.getRealEndDate();
    
    //when
    when(operationRepository.save(o)).thenReturn(o);
    
    operationService.finishOperation(o);
    
    //then
    assertFalse(o.isInWork());
    assertTrue(o.isEnded());
    assertNotNull(o.getRealEndDate());
    assertNotEquals(previousEndDate, o.getRealEndDate());
    verify(operationRepository).save(o);
  }
  
  @Test
  void startNextOperation_WithNextOperation_ShouldSetPropertiesAndSaveWhenRemainingOpIsOne() {
    //given
    Operation currentOperation = o;
    currentOperation.setEnded(true);
    List<Operation> operations = new ArrayList<>();
    
    Operation nextOperation = new Operation();
    nextOperation.setPriority(10);
    nextOperation.setProject(p);
    
    operations.add(currentOperation);
    operations.add(nextOperation);
    p.setOperations(operations);
    
    //when
    operationService.startNextOperation(currentOperation);
    
    //then
    assertTrue(nextOperation.isReadyToAcceptance());
    assertNotNull(nextOperation.getStartDate());
    assertEquals(24, nextOperation.getPeriod());
    assertNotNull(nextOperation.getPlannedEndDate());
    verify(operationRepository).save(nextOperation);
  }
  
  @Test
  void startNextOperation_WithNextOperation_ShouldSetPropertiesAndSave() {
    //given
    Operation currentOperation = o;
    currentOperation.setEnded(true);
    List<Operation> operations = new ArrayList<>();
    
    p.setOperationPeriod(60);
    
    Operation oneMoreOperation = new Operation();
    oneMoreOperation.setPriority(20);
    oneMoreOperation.setEnded(false);
    
    Operation nextOperation = new Operation();
    nextOperation.setPriority(10);
    nextOperation.setProject(p);
    
    operations.add(currentOperation);
    operations.add(nextOperation);
    operations.add(oneMoreOperation);
    p.setOperations(operations);
    
    //when
    operationService.startNextOperation(currentOperation);
    
    //then
    assertTrue(nextOperation.isReadyToAcceptance());
    assertNotNull(nextOperation.getStartDate());
    assertEquals(opPeriod, nextOperation.getPeriod());
    assertNotNull(nextOperation.getPlannedEndDate());
    verify(operationRepository).save(nextOperation);
  }
  
  @Test
  void getOperationsByProjectIdForEmp_ShouldReturnSortedOperationForEmpDtoList() {
    //given
    long projectId = 1L;
    
    Operation operation1 = new Operation();
    operation1.setId(1L);
    operation1.setPriority(2);
    operation1.setName("test_name1");
    operation1.setReadyToAcceptance(true);
    operation1.setEnded(false);
    operation1.setInWork(true);
    operation1.setEmployee(null);
    
    Operation operation2 = new Operation();
    operation2.setId(2L);
    operation2.setPriority(1);
    operation2.setName("test_name2");
    operation2.setReadyToAcceptance(false);
    operation2.setEnded(true);
    operation2.setInWork(false);
    operation2.setEmployee(null);
    
    List<Operation> operations = new ArrayList<>();
    operations.add(operation1);
    operations.add(operation2);
    
    //when
    when(operationRepository.findByProjectId(projectId)).thenReturn(operations);
    
    List<OperationForEmpDto> result = operationService.getOperationsByProjectIdForEmp(projectId);
    
    //then
    assertEquals(2, result.size());
    assertEquals(2L, result.get(0).id());
    assertEquals(1L, result.get(1).id());
  }
  
  @Test
  void getOperationsByProjectIdForEmp_ShouldReturnSortedOperationForEmpDtoList_WithEmployee() {
    //given
    long projectId = 1L;
    e.setFirstName("test_first_name");
    e.setLastName("test_last_name");
    
    Operation operation1 = new Operation();
    operation1.setId(1L);
    operation1.setPriority(2);
    operation1.setName("test_name1");
    operation1.setReadyToAcceptance(true);
    operation1.setEnded(false);
    operation1.setInWork(true);
    operation1.setEmployee(e);
    
    Operation operation2 = new Operation();
    operation2.setId(2L);
    operation2.setPriority(1);
    operation2.setName("test_name2");
    operation2.setReadyToAcceptance(false);
    operation2.setEnded(true);
    operation2.setInWork(false);
    operation2.setEmployee(e);
    
    List<Operation> operations = new ArrayList<>();
    operations.add(operation1);
    operations.add(operation2);
    
    //when
    when(operationRepository.findByProjectId(projectId)).thenReturn(operations);
    
    List<OperationForEmpDto> result = operationService.getOperationsByProjectIdForEmp(projectId);
    
    //then
    assertEquals(2, result.size());
    assertEquals(2L, result.get(0).id());
    assertEquals(1L, result.get(1).id());
    assertEquals("test_first_name", result.get(1).employeeFirstName());
    assertEquals("test_last_name", result.get(0).employeeLastName());
  }
  
  @Test
  void getOperationsInWorkByEmpIdForEmp_ShouldReturnOperationsInWorkForEmpDtoList() {
    //given
    Operation operation1 = new Operation();
    operation1.setId(1L);
    operation1.setName("Operation 1");
    
    Operation operation2 = new Operation();
    operation2.setId(2L);
    operation2.setName("Operation 2");
    
    Project project1 = new Project();
    project1.setId(1L);
    project1.setNumber(100);
    project1.setName("Project 1");
    project1.setCustomer("Customer 1");
    
    Project project2 = new Project();
    project2.setId(2L);
    project2.setNumber(101);
    project2.setName("Project 2");
    project2.setCustomer("Customer 2");
    
    operation1.setProject(project1);
    operation2.setProject(project2);
    
    List<Operation> operations = new ArrayList<>();
    operations.add(operation1);
    operations.add(operation2);
    
    //when
    when(operationRepository.findByEmpIdAndInWork(employeeId)).thenReturn(operations);
    
    List<OperationInWorkForEmpDto> result = operationService.getOperationsInWorkByEmpIdForEmp(employeeId);
    
    //then
    assertEquals(2, result.size());
    assertEquals(1L, result.get(0).operationId());
    assertEquals(1L, result.get(0).projectId());
    assertEquals(100, result.get(0).projectNumber());
    assertEquals("Project 1", result.get(0).projectName());
    assertEquals("Operation 1", result.get(0).operationName());
    assertEquals("Customer 1", result.get(0).customerLastName());
    
    assertEquals(2L, result.get(1).operationId());
    assertEquals(2L, result.get(1).projectId());
    assertEquals(101, result.get(1).projectNumber());
    assertEquals("Project 2", result.get(1).projectName());
    assertEquals("Operation 2", result.get(1).operationName());
    assertEquals("Customer 2", result.get(1).customerLastName());
  }
  
  @Test
  void deleteOperation_WhenOperationExists_ShouldDeleteOperation() {
    //given
    long operationId = 1L;
    
    //when
    when(operationRepository.existsById(operationId)).thenReturn(true);
    when(operationRepository.existsByIdOrIsEndedOrInWorkOrReadyToAcceptance(
        operationId, true, true, true)).thenReturn(false);
    when(operationRepository.existsByTypeWorkIdEqualsShipment(operationId, 1)).thenReturn(false);
    
    operationService.deleteOperation(operationId);
    
    //then
    verify(operationRepository).deleteById(operationId);
  }
  
  
  @Test
  void deleteOperation_WhenOperationNotFound_ShouldThrowOperationException() {
    //when
    when(operationRepository.existsById(operationId)).thenReturn(false);
    
    //then
    assertThrows(OperationException.class, () -> operationService.deleteOperation(operationId));
  }
  
  @Test
  void deleteOperation_WhenOperationInWorkOrReadyToAcceptance_ShouldThrowOperationException() {
    //when
    when(operationRepository.existsById(operationId)).thenReturn(true);
    when(operationRepository.existsByIdOrIsEndedOrInWorkOrReadyToAcceptance(
        operationId, true, true, true)).thenReturn(true);
    
    //then
    assertThrows(OperationException.class, () -> operationService.deleteOperation(operationId));
  }
  
  @Test
  void deleteOperation_WhenOperationIsShipment_ShouldThrowOperationException() {
    //when
    when(operationRepository.existsById(operationId)).thenReturn(true);
    when(operationRepository.existsByIdOrIsEndedOrInWorkOrReadyToAcceptance(
        operationId, true, true, true)).thenReturn(false);
    when(operationRepository.existsByTypeWorkIdEqualsShipment(operationId, 1)).thenReturn(true);
    
    //then
    assertThrows(OperationException.class, () -> operationService.deleteOperation(operationId));
  }
  
  @Test
  void closeOperation_WhenOperationInWork_ShouldUpdateOperationAndStartNextOperation() {
    //given
    o.setInWork(true);
    o.setReadyToAcceptance(false);
    
    //when
    operationService.closeOperation(o);
    
    //then
    verify(operationRepository).updateRealEndDateAndIsEndedAndReadyToAcceptanceAndInWorkById(
        any(LocalDateTime.class), eq(true), eq(false), eq(false), eq(o.getId()));
  }
  
  @Test
  void closeOperation_WhenOperationReadyToAcceptance_ShouldUpdateOperationAndStartNextOperation() {
    //given
    o.setInWork(false);
    o.setReadyToAcceptance(true);
    
    //when
    operationService.closeOperation(o);
    
    //then
    verify(operationRepository).updateRealEndDateAndIsEndedAndReadyToAcceptanceAndInWorkById(
        any(LocalDateTime.class), eq(true), eq(false), eq(false), eq(o.getId()));
  }
  
  @Test
  void closeOperation_WhenOperationInWorkOrReadyToAcceptanceFalse_ThrowException() {
    //given
    o.setInWork(false);
    o.setReadyToAcceptance(false);
    
    OperationException operationException = assertThrows(OperationException.class,
        () -> operationService.closeOperation(o));
    
    //then
    assertEquals(HttpStatus.BAD_REQUEST, operationException.getStatus());
    assertEquals("The operation is not yet in operation or is not available for acceptance",
        operationException.getMessage());
  }
  
  @Test
  void checkConfirmingEmployee_WhenEmployeeIdMatches_ShouldNotThrowException() {
    //given
    Operation operation = new Operation();
    Employee employee = new Employee();
    employee.setId(1L);
    operation.setEmployee(employee);
    long confirmingEmpId = 1L;
    
    //then
    assertDoesNotThrow(() -> operationService.checkConfirmingEmployee(operation, confirmingEmpId));
  }
  
  @Test
  void checkConfirmingEmployee_WhenEmployeeIdDoesNotMatch_ShouldThrowOperationException() {
    //given
    Operation operation = new Operation();
    Employee employee = new Employee();
    employee.setId(1L);
    operation.setEmployee(employee);
    long confirmingEmpId = 2L;
    
    OperationException operationException = assertThrows(OperationException.class,
        () -> operationService.checkConfirmingEmployee(operation, confirmingEmpId));
    
    //then
    assertEquals(HttpStatus.BAD_REQUEST, operationException.getStatus());
    assertEquals("The ID of the confirming employee is not equal"
            + " to the ID of the person who accepted the operation",
        operationException.getMessage());
  }
  
  @Test
  void checkConfirmingEmployee_WhenEmployeeNull_ShouldThrowOperationException() {
    //given
    Operation operation = new Operation();
    operation.setEmployee(null);
    long confirmingEmpId = 2L;
    
    OperationException operationException = assertThrows(OperationException.class,
        () -> operationService.checkConfirmingEmployee(operation, confirmingEmpId));
    
    //then
    assertEquals(HttpStatus.BAD_REQUEST, operationException.getStatus());
    assertEquals("The ID of the confirming employee is not equal"
            + " to the ID of the person who accepted the operation",
        operationException.getMessage());
  }
  
  @Test
  void getOperationIdNameProjectNumberDtoList_WhenProjectIdsAndEmployeeIdsProvided_ShouldReturnFilteredResult() {
    //given
    Set<Long> projectIds = new HashSet<>();
    projectIds.add(1L);
    projectIds.add(2L);
    
    Set<Long> employeeIds = new HashSet<>();
    employeeIds.add(10L);
    employeeIds.add(20L);
    
    LocalDate startOfPeriod = LocalDate.of(2023, 1, 1);
    LocalDate endOfPeriod = LocalDate.of(2023, 1, 31);
    
    List<OperationIdNameProjectNumberDto> expectedResults = new ArrayList<>();
    
    //when
    when(operationRepository.findByPeriodAndEmployeeAndProjectIds(startOfPeriod, endOfPeriod, employeeIds, projectIds))
        .thenReturn(expectedResults);
    
    List<OperationIdNameProjectNumberDto> result = operationService.getOperationIdNameProjectNumberDtoList(
        projectIds, employeeIds, startOfPeriod, endOfPeriod);
    
    //then
    assertEquals(expectedResults, result);
  }
  
  @Test
  void getOperationIdNameProjectNumberDtoList_WhenEmployeeIdsProvided_ShouldReturnFilteredResult() {
    //given
    Set<Long> employeeIds = new HashSet<>();
    employeeIds.add(10L);
    employeeIds.add(20L);
    
    LocalDate startOfPeriod = LocalDate.of(2023, 1, 1);
    LocalDate endOfPeriod = LocalDate.of(2023, 1, 31);
    
    List<OperationIdNameProjectNumberDto> expectedResults = new ArrayList<>();
    
    //when
    when(operationRepository.findByPeriodAndEmployeeIds(startOfPeriod, endOfPeriod, employeeIds))
        .thenReturn(expectedResults);
    
    // When
    List<OperationIdNameProjectNumberDto> result = operationService.getOperationIdNameProjectNumberDtoList(
        null, employeeIds, startOfPeriod, endOfPeriod);
    
    //then
    assertEquals(expectedResults, result);
  }
  
  @Test
  void getOperationIdNameProjectNumberDtoList_WhenEmployeeIdsProvidedAndProjectIdsAreEmpty_ShouldReturnFilteredResult() {
    //given
    Set<Long> employeeIds = new HashSet<>();
    employeeIds.add(10L);
    employeeIds.add(20L);
    
    LocalDate startOfPeriod = LocalDate.of(2023, 1, 1);
    LocalDate endOfPeriod = LocalDate.of(2023, 1, 31);
    
    List<OperationIdNameProjectNumberDto> expectedResults = new ArrayList<>();
    
    //when
    when(operationRepository.findByPeriodAndEmployeeIds(startOfPeriod, endOfPeriod, employeeIds))
        .thenReturn(expectedResults);
    
    // When
    List<OperationIdNameProjectNumberDto> result = operationService.getOperationIdNameProjectNumberDtoList(
        Collections.emptySet(), employeeIds, startOfPeriod, endOfPeriod);
    
    //then
    assertEquals(expectedResults, result);
  }
  
  @Test
  void getOperationIdNameProjectNumberDtoList_WhenProjectIdsProvided_ShouldReturnFilteredResult() {
    //given
    Set<Long> projectIds = new HashSet<>();
    projectIds.add(1L);
    projectIds.add(2L);
    
    LocalDate startOfPeriod = LocalDate.of(2023, 1, 1);
    LocalDate endOfPeriod = LocalDate.of(2023, 1, 31);
    
    List<OperationIdNameProjectNumberDto> expectedResults = new ArrayList<>();
    
    //when
    when(operationRepository.findByPeriodAndProjectIds(startOfPeriod, endOfPeriod, projectIds))
        .thenReturn(expectedResults);
    
    List<OperationIdNameProjectNumberDto> result = operationService.getOperationIdNameProjectNumberDtoList(
        projectIds, null, startOfPeriod, endOfPeriod);
    
    //then
    assertEquals(expectedResults, result);
  }
  
  @Test
  void getOperationIdNameProjectNumberDtoList_WhenNoProjectIdsAndEmployeeIdsProvided_ShouldReturnAllResults() {
    //given
    LocalDate startOfPeriod = LocalDate.of(2023, 1, 1);
    LocalDate endOfPeriod = LocalDate.of(2023, 1, 31);
    
    List<OperationIdNameProjectNumberDto> expectedResults = new ArrayList<>();
    
    //when
    when(operationRepository.findByPeriod(startOfPeriod, endOfPeriod))
        .thenReturn(expectedResults);
    
    List<OperationIdNameProjectNumberDto> result = operationService.getOperationIdNameProjectNumberDtoList(
        null, null, startOfPeriod, endOfPeriod);
    
    //then
    assertEquals(expectedResults, result);
  }
  
  @Test
  void getOperationIdNameProjectNumberDtoList_WhenNoProjectIdsAndEmployeeIdsProvidedNoDates_ShouldReturnAllResults() {
    //given
    List<OperationIdNameProjectNumberDto> expectedResults = new ArrayList<>();
    
    //when
    when(operationRepository.findByPeriod(null, null))
        .thenReturn(expectedResults);
    
    List<OperationIdNameProjectNumberDto> result = operationService.getOperationIdNameProjectNumberDtoList(
        null, null, null, null);
    
    //then
    assertEquals(expectedResults, result);
  }
  
  @Test
  void getOperationIdNameProjectNumberDtoList_WhenNoProjectIdsAndEmployeeIdsProvidedWithStarDate_ShouldReturnAllResults() {
    //given
    LocalDate startOfPeriod = LocalDate.of(2023, 1, 1);
    List<OperationIdNameProjectNumberDto> expectedResults = new ArrayList<>();
    
    //when
    when(operationRepository.findByPeriod(startOfPeriod, null))
        .thenReturn(expectedResults);
    
    List<OperationIdNameProjectNumberDto> result = operationService.getOperationIdNameProjectNumberDtoList(
        null, null, startOfPeriod, null);
    
    //then
    assertEquals(expectedResults, result);
  }
  
  @Test
  void getOperationIdNameProjectNumberDtoList_WhenProjectIdsAndEmployeeIdsAreEmpty_ShouldReturnAllResults() {
    //given
    LocalDate startOfPeriod = LocalDate.of(2023, 1, 1);
    LocalDate endOfPeriod = LocalDate.of(2023, 1, 31);
    
    List<OperationIdNameProjectNumberDto> expectedResults = new ArrayList<>();
    
    //when
    when(operationRepository.findByPeriod(startOfPeriod, endOfPeriod))
        .thenReturn(expectedResults);
    
    List<OperationIdNameProjectNumberDto> result = operationService.getOperationIdNameProjectNumberDtoList(
        Collections.emptySet(), Collections.emptySet(), startOfPeriod, endOfPeriod);
    
    //then
    assertEquals(expectedResults, result);
  }
  
  @Test
  void getOperationIdNameProjectNumberDtoList_WhenDatesIsIncorrect_ShouldThrowException() {
    //given
    LocalDate startOfPeriod = LocalDate.of(2023, 1, 31);
    LocalDate endOfPeriod = LocalDate.of(2023, 1, 1);
    
    ProjectException exception = assertThrows(ProjectException.class,
        () -> operationService.getOperationIdNameProjectNumberDtoList(
            Collections.emptySet(), Collections.emptySet(), startOfPeriod, endOfPeriod));
    
    //then
    assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    assertEquals("Start date cannot be after end date.", exception.getMessage());
  }
  
  @Test
  void getOperationsByIds_WhenOperationIdsProvided_ShouldReturnMatchingOperations() {
    //given
    Set<Long> operationIds = new HashSet<>();
    operationIds.add(1L);
    operationIds.add(2L);
    
    List<Operation> expectedOperations = new ArrayList<>();
    
    //when
    when(operationRepository.findOpsByIds(operationIds)).thenReturn(expectedOperations);
    
    List<Operation> result = operationService.getOperationsByIds(operationIds);
    
    //then
    assertEquals(expectedOperations, result);
  }
  
  @Test
  void insertNewOperation_WhenAllOperationsEnded_ShouldCreateNewOperationAndCheckAndUpdateShipmentOp() {
    //given
    InsertingOperationDto dto = new InsertingOperationDto(1L, "new_op", 1L, 25);
    
    Project p = new Project();
    p.setOperationPeriod(opPeriod);
    
    List<Operation> operations = new ArrayList<>();
    Operation operation1 = new Operation();
    operation1.setEnded(true);
    operations.add(operation1);
    
    Operation operation2 = new Operation();
    operation2.setEnded(true);
    operations.add(operation2);
    
    p.setOperations(operations);
    
    Operation newOperation = new Operation();
    
    //when
    when(operationFactory.create(eq(p), eq(dto.name()), eq(p.getOperationPeriod()), eq(dto.priority()),
        any(LocalDateTime.class), eq(true), eq(dto.typeWorkId()))).thenReturn(newOperation);
    
    boolean result = operationService.insertNewOperation(dto, p);
    
    //then
    assertTrue(result);
    verify(operationRepository).save(newOperation);
  }
  
  
  @Test
  void insertNewOperation_WhenSomeOperationsNotEnded_ShouldCreateNewOperationWithoutStartDateAndCheckAndUpdateShipmentOp() {
    //given
    InsertingOperationDto dto = new InsertingOperationDto(1L, "new_op", 1L, 25);
    
    Project p = new Project();
    p.setOperationPeriod(opPeriod);
    
    List<Operation> operations = new ArrayList<>();
    Operation operation1 = new Operation();
    operation1.setEnded(false);
    operations.add(operation1);
    
    Operation operation2 = new Operation();
    operation2.setEnded(true);
    operations.add(operation2);
    
    p.setOperations(operations);
    
    Operation newOperation = new Operation();
    
    //when
    when(operationFactory.create(p, dto.name(), 0, dto.priority(),
        null, false, dto.typeWorkId())).thenReturn(newOperation);
    
    boolean result = operationService.insertNewOperation(dto, p);
    
    //then
    assertTrue(result);
    verify(operationRepository).save(newOperation);
  }
  
  @Test
  void insertNewOperation_WhenExistingOperationInWorkOrReadyToAcceptance_ShouldCreateNewOperationAndReturnFalse() {
    //given
    InsertingOperationDto dto = new InsertingOperationDto(1L, "new_op", 1L, 25);
    
    Project p = new Project();
    p.setOperationPeriod(opPeriod);
    
    List<Operation> operations = new ArrayList<>();
    Operation operation1 = new Operation();
    operation1.setInWork(true);
    operation1.setPriority(10);
    operations.add(operation1);
    
    Operation operation2 = new Operation();
    operation2.setReadyToAcceptance(true);
    operation2.setPriority(35);
    operations.add(operation2);
    
    p.setOperations(operations);
    
    Operation newOperation = new Operation();
    when(operationFactory.create(
        p, dto.name(), 0, dto.priority(), null, false, dto.typeWorkId())
    ).thenReturn(newOperation);
    
    //when
    boolean result = operationService.insertNewOperation(dto, p);
    
    //then
    assertFalse(result);
    verify(operationRepository).save(newOperation);
    verify(operationRepository, never()).updatePriorityById(anyInt(), anyLong());
  }
  
  @Test
  void insertNewOperation_ShouldThrowExceptionOpWithPriorityAlreadyExists() {
    //given
    InsertingOperationDto dto = new InsertingOperationDto(1L, "new_op", 1L, 25);
    
    Project p = new Project();
    p.setOperationPeriod(opPeriod);
    
    List<Operation> operations = new ArrayList<>();
    Operation operation1 = new Operation();
    operation1.setInWork(true);
    operation1.setPriority(25);
    operations.add(operation1);
    
    Operation operation2 = new Operation();
    operation2.setReadyToAcceptance(true);
    operation2.setPriority(35);
    operations.add(operation2);
    
    p.setOperations(operations);
    
    
    //when
    OperationException exception = assertThrows(OperationException.class,
        () -> operationService.insertNewOperation(dto, p));
    
    //then
    assertEquals(HttpStatus.CONFLICT, exception.getStatus());
    assertEquals("The operation with priority: " + 25 + " already exists", exception.getMessage());
  }
  
  @Test
  void insertNewOperation_ShouldThrowExceptionLowPriority() {
    //given
    InsertingOperationDto dto = new InsertingOperationDto(1L, "new_op", 1L, 10);
    
    Project p = new Project();
    p.setOperationPeriod(opPeriod);
    
    List<Operation> operations = new ArrayList<>();
    Operation operation1 = new Operation();
    operation1.setReadyToAcceptance(true);
    operation1.setPriority(25);
    operations.add(operation1);
    
    Operation operation2 = new Operation();
    operation2.setReadyToAcceptance(false);
    operation2.setPriority(35);
    operations.add(operation2);
    
    p.setOperations(operations);
    
    
    //when
    OperationException exception = assertThrows(OperationException.class,
        () -> operationService.insertNewOperation(dto, p));
    
    //then
    assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    assertEquals("The priority of the operation: " + 10
        + " cannot be lower than the priority of the active operation: "
        + 25, exception.getMessage());
  }
  
  @Test
  void checkCorrectIdAndPriority_WhenOperationExists_ShouldNotThrowException() {
    //given
    long operationId = 1L;
    int operationPriority = 2;
    
    //when
    when(operationRepository.existsByIdAndPriority(operationId, operationPriority))
        .thenReturn(true);
    
    //then
    assertDoesNotThrow(() -> operationService.checkCorrectIdAndPriority(operationId, operationPriority));
  }
  
  @Test
  void checkCorrectIdAndPriority_WhenOperationDoesNotExist_ShouldThrowException() {
    //given
    long operationId = 1L;
    int operationPriority = 2;
    
    //when
    when(operationRepository.existsByIdAndPriority(operationId, operationPriority))
        .thenReturn(false);
    
    //then
    assertThrows(OperationException.class, () ->
        operationService.checkCorrectIdAndPriority(operationId, operationPriority));
  }
  
  @Test
  void checkIfOpAlreadyFinishedOrClosed_WhenOperationIsEnded_ShouldThrowException() {
    //given
    Operation operation = new Operation();
    operation.setId(1L);
    operation.setEnded(true);
    
    //then
    assertThrows(OperationException.class, () ->
        operationService.checkIfOpAlreadyFinishedOrClosed(operation));
    assertThrows(OperationException.class, () ->
        operationService.checkIfOpAlreadyFinishedOrClosed(operation), HttpStatus.CONFLICT.toString());
  }
  
  @Test
  void checkIfOpAlreadyFinishedOrClosed_WhenOperationIsNotEnded_ShouldNotThrowException() {
    //given
    Operation operation = new Operation();
    operation.setId(1L);
    operation.setEnded(false);
    
    //then
    assertDoesNotThrow(() ->
        operationService.checkIfOpAlreadyFinishedOrClosed(operation));
  }
  
  
}
