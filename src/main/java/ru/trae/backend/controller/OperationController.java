package ru.trae.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.trae.backend.dto.operation.*;
import ru.trae.backend.service.OperationService;

import java.util.List;

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

    @GetMapping("/project-operations/{projectId}")
    public ResponseEntity<List<OperationDto>> shortOperationsByProject(@PathVariable long projectId) {
        return ResponseEntity.ok(operationService.getOpsDtoListByProject(projectId));
    }

    @GetMapping("/employee/project-operations/{projectId}")
    public ResponseEntity<List<OperationForEmpDto>> operationsByProjectIdForEmp(@PathVariable long projectId) {
        return ResponseEntity.ok(operationService.getOperationsByProjectIdForEmp(projectId));
    }

    @GetMapping("/employee/operations-in-work/{employeeId}")
    public ResponseEntity<List<OperationInWorkForEmpDto>> operationsInWorkByEmpIdForEmp(@PathVariable long employeeId) {
        return ResponseEntity.ok(operationService.getOperationsInWorkByEmpIdForEmp(employeeId));
    }

    @PostMapping("/employee/receive-operation")
    public ResponseEntity receiveOperation(@RequestBody ReqOpEmpIdDto dto) {
        operationService.receiveOperation(dto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/employee/finish-operation")
    public ResponseEntity finishOperation(@RequestBody ReqOpEmpIdDto dto) {
        operationService.finishOperation(dto);
        return ResponseEntity.ok().build();
    }
}
