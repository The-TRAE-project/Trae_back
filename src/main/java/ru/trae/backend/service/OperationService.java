package ru.trae.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.trae.backend.dto.mapper.OperationDtoMapper;
import ru.trae.backend.dto.mapper.ShortOperationDtoMapper;
import ru.trae.backend.dto.operation.OpEmpIdDto;
import ru.trae.backend.dto.operation.OperationDto;
import ru.trae.backend.dto.operation.ShortOperationDto;
import ru.trae.backend.dto.operation.WrapperNewOperationDto;
import ru.trae.backend.entity.task.Operation;
import ru.trae.backend.entity.task.Project;
import ru.trae.backend.entity.user.Employee;
import ru.trae.backend.exceptionhandler.exception.OperationException;
import ru.trae.backend.repository.OperationRepository;

import java.time.LocalDateTime;
import java.util.List;

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
        wrapper.operations().forEach(
                no -> {
                    Operation o = new Operation();
                    o.setProject(p);
                    o.setName(no.name());
                    o.setDescription(no.description());
                    o.setPeriod(p.getPeriod() / wrapper.operations().size());
                    o.setPriority(no.priority());
                    o.setStartDate(null);
                    o.setEndDate(null);
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
        o.setStartDate(LocalDateTime.now());

        operationRepository.save(o);
    }

    public void finishOperation(OpEmpIdDto dto) {
        Operation o = getOperationById(dto.id());
        if (o.getEmployee().getId() != dto.employeeId())
            throw new OperationException(HttpStatus.BAD_REQUEST, "ID подтверждающего работника не равен ID принявшего операцию");

        o.setInWork(false);
        o.setEnded(true);
        o.setEndDate(LocalDateTime.now());

        operationRepository.save(o);
    }
}
