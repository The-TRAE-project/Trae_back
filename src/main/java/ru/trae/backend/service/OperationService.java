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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.trae.backend.dto.operation.InsertingOperationDto;
import ru.trae.backend.dto.operation.NewOperationDto;
import ru.trae.backend.dto.operation.OperationForEmpDto;
import ru.trae.backend.dto.operation.OperationInWorkForEmpDto;
import ru.trae.backend.dto.operation.ReqOpEmpIdDto;
import ru.trae.backend.entity.TypeWork;
import ru.trae.backend.entity.task.Operation;
import ru.trae.backend.entity.task.Project;
import ru.trae.backend.entity.user.Employee;
import ru.trae.backend.exceptionhandler.exception.OperationException;
import ru.trae.backend.repository.OperationRepository;
import ru.trae.backend.util.Util;

/**
 * Service class for working with operation data.
 *
 * @author Vladimir Olennikov
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OperationService {
  private final OperationRepository operationRepository;
  private final EmployeeService employeeService;
  private final TypeWorkService typeWorkService;
  public static final int MIN_PERIOD_OPERATION = 24;
  public static final int SHIPMENT_PERIOD = 24;
  
  /**
   * Gets an operation by its ID.
   *
   * @param id The id of the operation.
   * @return A {@link Operation} object.
   * @throws OperationException If the operation doesn't exist.
   */
  public Operation getOperationById(long id) {
    return operationRepository.findById(id).orElseThrow(
        () -> new OperationException(HttpStatus.NOT_FOUND,
            "Operation with ID " + id + " not found"));
  }
  
  /**
   * This method saves new operations to the project.
   * If operations size is greater than 0, the first operation is created.
   * If operations size is greater than 1, the rest operations are created.
   * The first operation gets a start time and the status "Ready to acceptance".
   *
   * @param p          this is the project associated with the operations
   * @param operations this is the list of {@link NewOperationDto} to be saved
   * @return List of saved Operation objects
   */
  public List<Operation> saveNewOperations(Project p, List<NewOperationDto> operations) {
    if (operations == null || operations.isEmpty()) {
      return Collections.emptyList();
    }
    
    final List<Operation> savedOperations = new ArrayList<>();
    NewOperationDto dto = operations.get(0);
    
    int period = p.getOperationPeriod();
    
    Operation fo = prepareOperation(
        p, dto.name(), period, 0,
        LocalDateTime.now(),
        LocalDateTime.now().plusHours(period),
        true,
        typeWorkService.getTypeWorkById(dto.typeWorkId()));
    
    savedOperations.add(operationRepository.save(fo));
    
    if (operations.size() > 1) {
      operations.stream()
          .skip(1)
          .forEach(no -> {
            Operation o = prepareOperation(
                p, no.name(), 0, operations.indexOf(no) * 10,
                null, null,
                false, typeWorkService.getTypeWorkById(no.typeWorkId()));
            
            savedOperations.add(operationRepository.save(o));
          });
    }
    
    Operation shipment = prepareShipmentOp(p, operations.size() * 10);
    savedOperations.add(operationRepository.save(shipment));
    
    return savedOperations;
  }
  
  /**
   * Use to receive operation.
   *
   * @param dto contain employee id and operation id
   */
  public void receiveOperation(ReqOpEmpIdDto dto) {
    Employee e = employeeService.getEmployeeById(dto.employeeId());
    Operation o = getOperationById(dto.operationId());
    
    checkForAcceptance(o);
    checkCompatibilityTypeWork(o, e);
    
    o.setInWork(true);
    o.setReadyToAcceptance(false);
    o.setEmployee(e);
    o.setAcceptanceDate(LocalDateTime.now());
    
    operationRepository.save(o);
  }
  
  /**
   * Finishes the operation.
   *
   * @param o The operation to be finished.
   */
  public void finishOperation(Operation o) {
    o.setInWork(false);
    o.setEnded(true);
    o.setRealEndDate(LocalDateTime.now());
    
    Operation op = operationRepository.save(o);
    log.info("the employee with id {} has finished the operation with id {}",
        o.getEmployee().getId(), o.getId());
    
    startNextOperation(op);
  }
  
  /**
   * This is method sets the start date, the period, and the planned end date to the next operation
   * in the project. Recalculate the remaining period of the operation.
   * The operations in the project are sorted by priority.
   *
   * @param o current operation
   */
  public void startNextOperation(Operation o) {
    log.info("starting next operation...");
    List<Operation> operations = o.getProject().getOperations()
        .stream()
        .sorted(Comparator.comparing(Operation::getPriority))
        .toList();
    
    if (operations.indexOf(o) + 1 < operations.size()) {
      Operation nextOp = operations.get(operations.indexOf(o) + 1);
      log.info("next operation with id {} from project with id {}",
          nextOp.getId(), nextOp.getProject().getId());
      
      int operationPeriod = getOperationPeriod(nextOp, operations);
      log.info("period for next operation with id {} = {}", nextOp.getId(), operationPeriod);
      
      nextOp.setReadyToAcceptance(true);
      nextOp.setStartDate(LocalDateTime.now());
      nextOp.setPeriod(operationPeriod);
      nextOp.setPlannedEndDate(nextOp.getStartDate().plusHours(operationPeriod));
      
      operationRepository.save(nextOp);
      log.info("next operation with id {} started", nextOp.getId());
    }
  }
  
  /**
   * Retrieves the list of operations for the specified project in the {@link OperationForEmpDto}
   * format.
   *
   * @param projectId the project id
   * @return the list of operations for the specified project in the {@link OperationForEmpDto}
   *     format, sorted by priority
   */
  public List<OperationForEmpDto> getOperationsByProjectIdForEmp(long projectId) {
    List<Operation> operations = operationRepository.findByProjectId(projectId);
    
    return operations.stream()
        .sorted(Util::prioritySorting)
        .map(o -> new OperationForEmpDto(
            o.getId(),
            o.getPriority(),
            o.getName(),
            o.isReadyToAcceptance(),
            o.isEnded(),
            o.isInWork(),
            o.getEmployee() == null ? null : o.getEmployee().getFirstName(),
            o.getEmployee() == null ? null : o.getEmployee().getLastName()
        ))
        .toList();
  }
  
  /**
   * Retrieves a list of operations for a given employee, where the operation is in work.
   *
   * @param employeeId The ID of the employee for whom the operations should be retrieved
   * @return A list of objects containing the operation's ID, the project's ID and name,
   *     the operation's name and the customer name
   */
  public List<OperationInWorkForEmpDto> getOperationsInWorkByEmpIdForEmp(long employeeId) {
    List<Operation> operations = operationRepository.findByEmpIdAndInWork(employeeId);
    return operations.stream()
        .map(o -> new OperationInWorkForEmpDto(
            o.getId(),
            o.getProject().getId(),
            o.getProject().getNumber(),
            o.getProject().getName(),
            o.getName(),
            o.getProject().getCustomer()
        ))
        .toList();
  }
  
  /**
   * A method to insert new operation without close the active operations.
   *
   * @param dto the inserting operation dto
   * @param p   the project
   * @throws IllegalStateException    if the priority already exists
   * @throws IllegalArgumentException if the priority is not available
   */
  public boolean insertNewOperation(InsertingOperationDto dto, Project p) {
    List<Operation> operations = p.getOperations();
    
    checkExistsPriority(operations, dto.priority());
    checkAvailablePriority(operations, dto.priority());
    
    Operation newOp = prepareOperation(
        p, dto.name(), 0, dto.priority(),
        null, null,
        false, typeWorkService.getTypeWorkById(dto.typeWorkId()));
    
    operationRepository.save(newOp);
    
    return checkAndUpdateShipmentOp(operations, dto.priority());
  }
  
  /**
   * Deletes operation.
   *
   * @param operationId - operation ID
   * @throws OperationException if operation with given ID not found or cannot be deleted
   */
  @Transactional
  public void deleteOperation(long operationId) {
    if (!operationRepository.existsById(operationId)) {
      throw new OperationException(HttpStatus.NOT_FOUND,
          "Operation with ID " + operationId + " not found");
    }
    if (operationRepository.existsByIdOrIsEndedOrInWorkOrReadyToAcceptance(
        operationId, true, true, true)) {
      throw new OperationException(HttpStatus.BAD_REQUEST,
          "Operation with ID " + operationId + " cannot be deleted");
    }
    
    operationRepository.deleteById(operationId);
  }
  
  /**
   * Closes the operation.
   *
   * @param operationId id of the operation
   * @throws OperationException if the operation is already finished or is not yet in operation
   *                            or is not available for acceptance
   */
  public void closeOperation(long operationId) {
    Operation o = getOperationById(operationId);
    if (o.isEnded()) {
      throw new OperationException(HttpStatus.CONFLICT, "This operation is already finished");
    }
    
    if (o.isInWork() || o.isReadyToAcceptance()) {
      operationRepository.updateRealEndDateAndIsEndedAndReadyToAcceptanceAndInWorkById(
          LocalDateTime.now(), true, false, false, o.getId());
    } else {
      throw new OperationException(HttpStatus.BAD_REQUEST,
          "The operation is not yet in operation or is not available for acceptance");
    }
    
    startNextOperation(o);
  }
  
  private boolean checkAndUpdateShipmentOp(List<Operation> operations, int priority) {
    int maxPriority = operations.stream()
        .mapToInt(Operation::getPriority)
        .max().getAsInt();
    if (maxPriority < priority) {
      createOrUpdateShipmentOp(operations, maxPriority, priority);
      return true;
    }
    return false;
  }
  
  private void createOrUpdateShipmentOp(List<Operation> operations,
                                        int maxPriority, int priorityNewOp) {
    //здесь происходит поиск этапа "отгрузка" текущего проекта
    Operation lastOp = operations.stream().filter(o -> o.getPriority() == maxPriority)
        .findFirst()
        .orElseThrow(() -> new OperationException(HttpStatus.BAD_REQUEST,
            "Problem with operation priority"));
    //если отгрузка еще не доступна для принятия, не в работе или не выполнена,
    // то ее приоритет становится на 10 больше вставленной операции
    if (!lastOp.isEnded() && !lastOp.isInWork() && !lastOp.isReadyToAcceptance()
        && priorityNewOp > lastOp.getPriority()) {
      operationRepository.updatePriorityById(priorityNewOp + 10, lastOp.getId());
      //в другом случае создается новая отгрузка с приоритетом на 10 выше вставленной операции
    } else {
      Operation shipment = prepareShipmentOp(lastOp.getProject(), priorityNewOp + 10);
      operationRepository.save(shipment);
    }
  }
  
  private void checkExistsPriority(List<Operation> operations, int priority) {
    if (operations.stream().anyMatch(o -> o.getPriority() == priority)) {
      throw new OperationException(HttpStatus.CONFLICT,
          "The operation with priority: " + priority + " already exists");
    }
  }
  
  private void checkAvailablePriority(List<Operation> operations, int priority) {
    int actualPriority = operations.stream()
        .filter(o -> o.isInWork() || o.isReadyToAcceptance())
        .findFirst()
        .map(Operation::getPriority)
        .orElse(operations.size() * 10);
    if (actualPriority > priority) {
      throw new OperationException(HttpStatus.BAD_REQUEST,
          "The priority of the operation: " + priority
              + " cannot be lower than the priority of the active operation: "
              + actualPriority);
    }
  }
  
  private void checkForAcceptance(Operation o) {
    if (!o.isReadyToAcceptance()) {
      throw new OperationException(HttpStatus.BAD_REQUEST,
          "The operation is currently unavailable for acceptance.");
    }
  }
  
  private void checkCompatibilityTypeWork(Operation o, Employee e) {
    if (!e.getTypeWorks().contains(o.getTypeWork())) {
      throw new OperationException(HttpStatus.BAD_REQUEST, "Types of work are not compatible.");
    }
  }
  
  /**
   * Checks if the confirming employee is the same as the one who accepted the operation.
   *
   * @param o               The operation being processed.
   * @param confirmingEmpId The id of the confirming employee.
   * @throws OperationException If the IDs do not match.
   */
  public void checkConfirmingEmployee(Operation o, long confirmingEmpId) {
    if (o.getEmployee() == null || o.getEmployee().getId() != confirmingEmpId) {
      throw new OperationException(HttpStatus.BAD_REQUEST,
          "The ID of the confirming employee is not equal"
              + " to the ID of the person who accepted the operation");
    }
  }
  
  private int getOperationPeriod(Operation nextOp, List<Operation> operations) {
    long opRemaining = operations.stream().filter(op -> !op.isEnded()).count();
    
    // здесь отслеживается последний этап "отгрузка" = на него всегда 24 часа.
    if (opRemaining == 1) {
      return 24;
    } else {
      return nextOp.getProject().getOperationPeriod();
    }
  }
  
  private Operation prepareOperation(Project p, String name, int period, int priority,
                                     LocalDateTime start,
                                     LocalDateTime end,
                                     boolean ready, TypeWork typeWork
  ) {
    Operation o = new Operation();
    o.setProject(p);
    o.setName(name);
    o.setPeriod(period);
    o.setPriority(priority);
    o.setStartDate(start);
    o.setPlannedEndDate(end);
    o.setAcceptanceDate(null);
    o.setEnded(false);
    o.setInWork(false);
    o.setReadyToAcceptance(ready);
    o.setTypeWork(typeWork);
    
    return o;
  }
  
  private Operation prepareShipmentOp(Project p, int priority) {
    return prepareOperation(p, "Отгрузка", 24, priority,
        null, null,
        false, typeWorkService.getTypeWorkByName("Отгрузка"));
  }
  
  /**
   * Checks whether the priority of the operation matches its id.
   *
   * @param operationId       id of the operation
   * @param operationPriority priority of the operation
   */
  public void checkCorrectIdAndPriority(long operationId, int operationPriority) {
    if (!operationRepository.existsByIdAndPriority(operationId, operationPriority)) {
      throw new OperationException(HttpStatus.BAD_REQUEST,
          "The priority of the operation does not match the operation id");
    }
  }
}
