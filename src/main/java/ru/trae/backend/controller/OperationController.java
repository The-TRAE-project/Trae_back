package ru.trae.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.trae.backend.dto.WrapperNewOperationDto;
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
}
