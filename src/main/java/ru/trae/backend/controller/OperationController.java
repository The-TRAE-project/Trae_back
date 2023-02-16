package ru.trae.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.trae.backend.dto.operation.OpEmpIdDto;
import ru.trae.backend.dto.operation.ShortOperationDto;
import ru.trae.backend.dto.operation.WrapperNewOperationDto;
import ru.trae.backend.service.OperationService;
import ru.trae.backend.util.Operations;

import java.util.Arrays;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/operation")
public class OperationController {

    private final OperationService operationService;

    @GetMapping("/names")
    public ResponseEntity<List<String>> names() {
        return ResponseEntity.ok(Arrays.stream(Operations.values())
                .map(o -> o.value)
                .toList());
    }

    @PostMapping("/new")
    public ResponseEntity operationPersist(@RequestBody WrapperNewOperationDto wrapper) {
        operationService.saveNewOperations(wrapper);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/short-operations/{projectId}")
    public ResponseEntity<List<ShortOperationDto>> shortOperations(@PathVariable long projectId) {
        return ResponseEntity.ok(operationService.getShortOpDtoList(projectId));
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
