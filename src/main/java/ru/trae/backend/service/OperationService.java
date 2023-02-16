package ru.trae.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.trae.backend.dto.mapper.ShortOperationDtoMapper;
import ru.trae.backend.dto.operation.OperationDto;
import ru.trae.backend.dto.operation.ShortOperationDto;
import ru.trae.backend.dto.operation.WrapperNewOperationDto;
import ru.trae.backend.dto.mapper.OperationDtoMapper;
import ru.trae.backend.entity.task.Operation;
import ru.trae.backend.entity.task.Project;
import ru.trae.backend.exceptionhandler.exception.OperationException;
import ru.trae.backend.repository.OperationRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OperationService {
    private final OperationRepository operationRepository;
    private final ProjectService projectService;
    private final OperationDtoMapper operationDtoMapper;
    private final ShortOperationDtoMapper shortOperationDtoMapper;

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
                    o.setPeriod(no.period());
                    o.setPriority(no.priority());
                    o.setStartDate(LocalDateTime.now());
                    o.setEnded(false);
                    o.setInWork(false);

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
}
