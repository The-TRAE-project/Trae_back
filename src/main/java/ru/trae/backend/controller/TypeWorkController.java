package ru.trae.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.trae.backend.dto.TypeWorkDto;
import ru.trae.backend.service.TypeWorkService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/type-work")
public class TypeWorkController {
    private final TypeWorkService typeWorkService;

    @GetMapping("/types")
    public ResponseEntity<List<TypeWorkDto>> types() {
        return ResponseEntity.ok(typeWorkService.getTypes());
    }
}
