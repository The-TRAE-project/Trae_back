package ru.trae.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.trae.backend.dto.type.NewTypeWorkDto;
import ru.trae.backend.dto.type.TypeWorkDto;
import ru.trae.backend.entity.TypeWork;
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

    @PostMapping("/new")
    public ResponseEntity<TypeWorkDto> typeWorkPersist(@RequestBody NewTypeWorkDto dto) {
        typeWorkService.checkAvailableByName(dto.name());
        TypeWork tw = typeWorkService.saveNewTypeWork(dto);
        return ResponseEntity.ok(new TypeWorkDto(tw.getId(), tw.getName()));
    }
}
