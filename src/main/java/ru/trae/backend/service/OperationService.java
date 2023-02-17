package ru.trae.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.trae.backend.dto.mapper.OperationDtoMapper;
import ru.trae.backend.dto.mapper.ShortOperationDtoMapper;
import ru.trae.backend.dto.operation.*;
import ru.trae.backend.entity.task.Operation;
import ru.trae.backend.entity.task.Project;
import ru.trae.backend.entity.user.Employee;
import ru.trae.backend.exceptionhandler.exception.OperationException;
import ru.trae.backend.repository.OperationRepository;
import ru.trae.backend.util.NumbersUtil;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import static java.time.temporal.ChronoUnit.DAYS;

@Service
@RequiredArgsConstructor
public class OperationService {
    private final OperationRepository operationRepository;
    private final ProjectService projectService;
    private final EmployeeService employeeService;
    private final OperationDtoMapper operationDtoMapper;
    private final ShortOperationDtoMapper shortOperationDtoMapper;
    private final TypeWorkService typeWorkService;

    public Operation getOperationById(long id) {
        return operationRepository.findById(id).orElseThrow(
                () -> new OperationException(HttpStatus.NOT_FOUND, "Операция с ID " + id + " не найдена"));
    }

    public void saveNewOperations(WrapperNewOperationDto wrapper) {
        Project p = projectService.getProjectById(wrapper.projectId());

        List<NewOperationDto>  operations = wrapper.operations()
                .stream()
                .sorted(Comparator.comparing(NewOperationDto::priority))
                .toList();

        if (operations.size() > 0) {
            NewOperationDto dto = operations.get(0);

            Operation o = new Operation();
            o.setProject(p);
            o.setName(dto.name());
            o.setDescription(dto.description());
            o.setPeriod(NumbersUtil.getPeriodForFirstOperation(p.getPeriod(), wrapper.operations().size()));
            o.setPriority(dto.priority());
            o.setStartDate(LocalDateTime.now());
            o.setPlannedEndDate(LocalDateTime.now().plusDays(o.getPeriod()));
            o.setAcceptanceDate(null);
            o.setEnded(false);
            o.setInWork(false);
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
                                o.setTypeWork(typeWorkService.getTypeWorkById(no.typeWorkId()));

                                operationRepository.save(o);
                            });
    }

    public OperationDto getOperationDtoById(long id) {
        return operationDtoMapper.apply(getOperationById(id));
    }

    public List<ShortOperationDto> getShortOpDtoList(long projectId) {
        Project p = projectService.getProjectById(projectId);
        return p.getOperations().stream()
                .map(shortOperationDtoMapper)
                .toList();
    }

    public void receiveOperation(OpEmpIdDto dto) {
        Employee e = employeeService.getEmployeeById(dto.employeeId());
        Operation o = getOperationById(dto.id());

        o.setInWork(true);
        o.setEmployee(e);
        o.setAcceptanceDate(LocalDateTime.now());

        operationRepository.save(o);
    }

    public void finishOperation(OpEmpIdDto dto) {
        Operation o = getOperationById(dto.id());

        if (o.getEmployee().getId() != dto.employeeId())
            throw new OperationException(HttpStatus.BAD_REQUEST, "ID подтверждающего работника не равен ID принявшего операцию");

        o.setInWork(false);
        o.setEnded(true);
        o.setRealEndDate(LocalDateTime.now());

        Operation op = operationRepository.save(o);

        startNextOperation(op);
    }

    public void startNextOperation(Operation o) {
        List<Operation> operations = o.getProject().getOperations()
                .stream()
                .sorted(Comparator.comparing(Operation::getPriority))
                .toList();

        if (operations.indexOf(o) + 1 < operations.size()) {
            Operation nextOp = operations.get(operations.indexOf(o) + 1);

            long remainsPeriod = DAYS.between(LocalDateTime.now(), o.getProject().getPlannedEndDate());
            long opRemains = operations.stream().filter(op -> !op.isEnded()).count();
            int newPeriod = NumbersUtil.getPeriodForFirstOperation((int) remainsPeriod, (int) opRemains);

            nextOp.setPeriod(newPeriod);
            nextOp.setStartDate(LocalDateTime.now());
            nextOp.setPlannedEndDate(LocalDateTime.now().plusDays(newPeriod));

            operationRepository.save(nextOp);
        }
    }
}
