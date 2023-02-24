package ru.trae.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.trae.backend.dto.mapper.OperationDtoMapper;
import ru.trae.backend.dto.operation.*;
import ru.trae.backend.entity.task.Operation;
import ru.trae.backend.entity.task.Project;
import ru.trae.backend.entity.user.Employee;
import ru.trae.backend.exceptionhandler.exception.OperationException;
import ru.trae.backend.repository.OperationRepository;
import ru.trae.backend.util.Util;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import static java.time.temporal.ChronoUnit.HOURS;

@Service
@RequiredArgsConstructor
public class OperationService {
    private final OperationRepository operationRepository;
    private final ProjectService projectService;
    private final EmployeeService employeeService;
    private final OperationDtoMapper operationDtoMapper;
    private final TypeWorkService typeWorkService;

    public Operation getOperationById(long id) {
        return operationRepository.findById(id).orElseThrow(
                () -> new OperationException(HttpStatus.NOT_FOUND, "Операция с ID " + id + " не найдена"));
    }

    public void saveNewOperations(WrapperNewOperationDto wrapper) {
        Project p = projectService.getProjectById(wrapper.projectId());

        List<NewOperationDto> operations = wrapper.operations()
                .stream()
                .sorted(Comparator.comparing(NewOperationDto::priority))
                .toList();

        if (operations.size() > 0) {
            NewOperationDto dto = operations.get(0);

            Operation o = new Operation();
            o.setProject(p);
            o.setName(dto.name());
            o.setDescription(dto.description());
            o.setPeriod(Util.getPeriodForFirstOperation(p.getPeriod(), wrapper.operations().size()));
            o.setPriority(dto.priority());
            o.setStartDate(LocalDateTime.now());
            o.setPlannedEndDate(LocalDateTime.now().plusDays(o.getPeriod()));
            o.setAcceptanceDate(null);
            o.setEnded(false);
            o.setInWork(false);
            o.setReadyToAcceptance(true);
            o.setTypeWork(typeWorkService.getTypeWorkById(dto.typeWorkId()));

            operationRepository.save(o);
        }

        if (operations.size() > 1)
            operations.stream()
                    .skip(1)
                    .forEach(
                            no -> {
                                Operation o = new Operation();
                                o.setProject(p);
                                o.setName(no.name());
                                o.setDescription(no.description());
                                o.setPeriod(0);
                                o.setPriority(no.priority());
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

    public List<OperationDto> getOpsDtoListByProject(long projectId) {
        Project p = projectService.getProjectById(projectId);
        return p.getOperations().stream()
                .map(operationDtoMapper)
                .toList();
    }

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

    public void finishOperation(ReqOpEmpIdDto dto) {
        Operation o = getOperationById(dto.operationId());

        checkConfirmingEmployee(o, dto.employeeId());

        o.setInWork(false);
        o.setEnded(true);
        o.setRealEndDate(LocalDateTime.now());

        Operation op = operationRepository.save(o);

        checkAndUpdateProjectEndDate(op);

        startNextOperation(op);
    }

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

    public List<OperationForEmp> getOperationsByProjectIdForEmp(long projectId) {
        List<Operation> operations = operationRepository.findByProjectId(projectId);

        return operations.stream()
                .sorted(Util::prioritySorting)
                .map(o -> new OperationForEmp(
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

    private void checkForAcceptance(Operation o) {
        if (!o.isReadyToAcceptance())
            throw new OperationException(HttpStatus.BAD_REQUEST, "The operation is currently unavailable for acceptance.");
    }

    private void checkCompatibilityTypeWork(Operation o, Employee e) {
        if (!e.getTypeWorks().contains(o.getTypeWork()))
            throw new OperationException(HttpStatus.BAD_REQUEST, "Types of work are not compatible.");
    }

    private void checkConfirmingEmployee(Operation o, long confirmingEmpId) {
        if (o.getEmployee().getId() != confirmingEmpId)
            throw new OperationException(HttpStatus.BAD_REQUEST,
                    "The ID of the confirming employee is not equal to the ID of the person who accepted the operation");
    }

    private int recalculationRemainingPeriod(Operation nextOp, List<Operation> operations) {
        long remainingPeriod = HOURS.between(LocalDateTime.now(), nextOp.getProject().getPlannedEndDate());
        long opRemaining = operations.stream().filter(op -> !op.isEnded()).count();

        // здесь отслеживается последний этап "отгрузка" = на него всегда 24 часа.
        if (opRemaining == 1)
            return 24;
        // здесь вычитается из оставшися операций - "отгрузка" и время на нее - 24 часа.
        return Util.getPeriodForFirstOperation((int) remainingPeriod - 24, (int) opRemaining - 1);
    }

    private void checkAndUpdateProjectEndDate(Operation o) {
        if (o.getRealEndDate().isBefore(o.getPlannedEndDate())) return;

        long hours = HOURS.between(o.getPlannedEndDate(), o.getRealEndDate());
        Project p = o.getProject();
        LocalDateTime newPlannedEndDate = p.getPlannedEndDate().plusHours(hours);

        projectService.updatePlannedEndDate(newPlannedEndDate, p.getId());
    }
}
