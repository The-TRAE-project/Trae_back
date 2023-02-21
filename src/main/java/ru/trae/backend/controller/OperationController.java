package ru.trae.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.trae.backend.dto.operation.OpEmpIdDto;
import ru.trae.backend.dto.operation.ShortOperationDto;
import ru.trae.backend.dto.operation.WrapperNewOperationDto;
import ru.trae.backend.service.OperationService;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/operation")
public class OperationController {

    private final OperationService operationService;

    @PostMapping("/new")
    public ResponseEntity operationPersist(@RequestBody WrapperNewOperationDto wrapper) {
        operationService.saveNewOperations(wrapper);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/short-project-operations/{projectId}")
    public ResponseEntity<List<ShortOperationDto>> shortOperationsByProject(@PathVariable long projectId) {
        return ResponseEntity.ok(operationService.getShortOpDtoListByProject(projectId));
    }

    @GetMapping("/available-operations-for-employee/{employeeId}")
    public ResponseEntity<Map<String, List<ShortOperationDto>>> shortOperationsByEmployee(@PathVariable long employeeId) {
        return ResponseEntity.ok(operationService.getAvailableOperationByTypeWork(employeeId));
    }

    @PostMapping("/receive-operation")
    public ResponseEntity receiveOperation(@RequestBody OpEmpIdDto dto) {
        operationService.receiveOperation(dto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/finish-operation")
    public ResponseEntity finishOperation(@RequestBody OpEmpIdDto dto) {
        operationService.finishOperation(dto);
        return ResponseEntity.ok().build();
    }
}
