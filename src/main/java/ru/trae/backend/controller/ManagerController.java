package ru.trae.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.trae.backend.dto.jwt.JwtResponse;
import ru.trae.backend.dto.manager.ManagerDto;
import ru.trae.backend.dto.manager.ManagerRegisterDto;
import ru.trae.backend.entity.user.Manager;
import ru.trae.backend.service.ManagerService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/manager")
public class ManagerController {
    private final ManagerService managerService;

    @PostMapping("/register")
    public ResponseEntity<JwtResponse> register(@RequestBody ManagerRegisterDto dto) {
        managerService.checkAvailableEmail(dto.email());
        managerService.checkAvailableUsername(dto.username());
        return ResponseEntity.ok(managerService.saveNewManager(dto));
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
