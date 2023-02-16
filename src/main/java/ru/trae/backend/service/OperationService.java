package ru.trae.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.trae.backend.dto.OperationDto;
import ru.trae.backend.dto.ProjectDto;
import ru.trae.backend.dto.WrapperNewOperationDto;
import ru.trae.backend.dto.mapper.OperationDtoMapper;
import ru.trae.backend.entity.task.Operation;
import ru.trae.backend.entity.task.Project;
import ru.trae.backend.exceptionhandler.exception.OperationException;
import ru.trae.backend.exceptionhandler.exception.ProjectException;
import ru.trae.backend.repository.OperationRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OperationService {
    private final OperationRepository operationRepository;
    private final ProjectService projectService;
    private final OperationDtoMapper operationDtoMapper;

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

}
