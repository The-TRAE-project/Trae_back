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

import static java.time.temporal.ChronoUnit.HOURS;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.trae.backend.dto.mapper.OperationDtoMapper;
import ru.trae.backend.dto.operation.NewOperationDto;
import ru.trae.backend.dto.operation.OperationDto;
import ru.trae.backend.dto.operation.OperationForEmpDto;
import ru.trae.backend.dto.operation.OperationInWorkForEmpDto;
import ru.trae.backend.dto.operation.ReqOpEmpIdDto;
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
@Service
@RequiredArgsConstructor
public class OperationService {
  private final OperationRepository operationRepository;
  private final EmployeeService employeeService;
  private final OperationDtoMapper operationDtoMapper;
  private final TypeWorkService typeWorkService;

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
   */
  public void saveNewOperations(Project p, List<NewOperationDto> operations) {
    if (operations == null || operations.isEmpty()) {
      return;
    }

    NewOperationDto dto = operations.get(0);

    Operation fo = new Operation();
    fo.setProject(p);
    fo.setName(dto.name());
    fo.setPeriod(Util.getPeriodForFirstOperation(p.getPeriod() * 24, operations.size()) - 24);
    fo.setPriority(0);
    fo.setStartDate(LocalDateTime.now());
    fo.setPlannedEndDate(LocalDateTime.now().plusHours(fo.getPeriod()));
    fo.setAcceptanceDate(null);
    fo.setEnded(false);
    fo.setInWork(false);
    fo.setReadyToAcceptance(true);
    fo.setTypeWork(typeWorkService.getTypeWorkById(dto.typeWorkId()));

    operationRepository.save(fo);


    if (operations.size() > 1) {
      operations.stream()
              .skip(1)
              .forEach(
                      no -> {
                        Operation o = new Operation();
                        o.setProject(p);
                        o.setName(no.name());
                        o.setPeriod(0);
                        o.setPriority(operations.indexOf(no) * 10);
                        o.setStartDate(null);
                        o.setPlannedEndDate(null);
                        o.setAcceptanceDate(null);
                        o.setEnded(false);
                        o.setInWork(false);
                        o.setReadyToAcceptance(false);
                        o.setTypeWork(typeWorkService.getTypeWorkById(no.typeWorkId()));

                        operationRepository.save(o);
                      });
    }

    Operation lo = new Operation();
    lo.setProject(p);
    lo.setName("Отгрузка");
    lo.setPeriod(0);
    lo.setPriority(operations.size() * 10);
    lo.setStartDate(null);
    lo.setPlannedEndDate(null);
    lo.setAcceptanceDate(null);
    lo.setEnded(false);
    lo.setInWork(false);
    lo.setReadyToAcceptance(false);
    lo.setTypeWork(typeWorkService.getTypeWorkByName("Отгрузка"));

    operationRepository.save(lo);
  }

  /**
   * Gets the list of {@link OperationDto} objects by project id.
   *
   * @param projectId the id of the project
   * @return the list of {@link OperationDto} objects
   */
  public List<OperationDto> getOpsDtoListByProject(long projectId) {
    return operationRepository.findByProjectIdOrderByPriorityAsc(projectId).stream()
            .map(operationDtoMapper)
            .toList();
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
    List<Operation> operations = o.getProject().getOperations()
            .stream()
            .sorted(Comparator.comparing(Operation::getPriority))
            .toList();

    if (operations.indexOf(o) + 1 < operations.size()) {
      Operation nextOp = operations.get(operations.indexOf(o) + 1);

      int newPeriod = recalculationRemainingPeriod(nextOp, operations);

      nextOp.setReadyToAcceptance(true);
      nextOp.setPeriod(newPeriod);
      nextOp.setStartDate(LocalDateTime.now());
      nextOp.setPlannedEndDate(LocalDateTime.now().plusHours(newPeriod));

      operationRepository.save(nextOp);
    }
  }

  /**
   * Retrieves the list of operations for the specified project in the {@link OperationForEmpDto}
   * format.
   *
   * @param projectId the project id
   * @return the list of operations for the specified project in the {@link OperationForEmpDto}
   *         format, sorted by priority
   */
  public List<OperationForEmpDto> getOperationsByProjectIdForEmp(long projectId) {
    List<Operation> operations = operationRepository.findByProjectId(projectId);

    return operations.stream()
            .sorted(Util::prioritySorting)
            .map(o -> new OperationForEmpDto(
                    o.getId(),
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
   *         the operation's name and the customer name
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

  private int recalculationRemainingPeriod(Operation nextOp, List<Operation> operations) {
    long remainingPeriod = HOURS.between(LocalDateTime.now(),
            nextOp.getProject().getPlannedEndDate());
    long opRemaining = operations.stream().filter(op -> !op.isEnded()).count();

    // здесь отслеживается последний этап "отгрузка" = на него всегда 24 часа.
    if (opRemaining == 1) {
      return 24;
    }
    // здесь вычитается из оставшися операций - "отгрузка" и время на нее - 24 часа.
    return Util.getPeriodForFirstOperation((int) remainingPeriod - 24, (int) opRemaining - 1);
  }
}
