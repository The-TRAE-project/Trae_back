package ru.trae.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.trae.backend.dto.ManagerDto;
import ru.trae.backend.dto.ManagerRegisterDto;
import ru.trae.backend.entity.user.Manager;
import ru.trae.backend.service.ManagerService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/manager")
public class ManagerController {
    private final ManagerService managerService;

    @PostMapping("/register")
    public ResponseEntity<ManagerDto> register(@RequestBody ManagerRegisterDto dto) {
        Manager m = managerService.saveNewManager(dto);
        return ResponseEntity.ok(managerService.convertFromManager(m));
    }

    @GetMapping("/{id}")
    private ResponseEntity<ManagerDto> manager(@PathVariable long id) {
        Manager m = managerService.getManagerById(id);
        return ResponseEntity.ok(managerService.convertFromManager(m));
    }

    @GetMapping("/managers")
    public ResponseEntity<List<ManagerDto>> managers() {
        return ResponseEntity.ok(managerService.getAllManagers());
    }
}
