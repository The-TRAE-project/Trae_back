package ru.trae.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.trae.backend.dto.NewOrderDto;
import ru.trae.backend.service.ManagerService;
import ru.trae.backend.service.OrderService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/order")
public class OrderController {

    private final OrderService orderService;
    private final ManagerService managerService;

    @PostMapping("/new")
    public ResponseEntity orderHandler(@RequestBody NewOrderDto dto) {

        orderService.receiveNewOrder(dto, managerService.getManager(dto.managerId()));
        return ResponseEntity.ok().build();
    }
}
