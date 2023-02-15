package ru.trae.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.trae.backend.dto.NewOrderDto;
import ru.trae.backend.dto.OrderDto;
import ru.trae.backend.service.OrderService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/order")
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/new")
    public ResponseEntity<OrderDto> orderHandler(@RequestBody NewOrderDto dto) {

        OrderDto orderDto = orderService.convertFromOrder(orderService.receiveNewOrder(dto));
        return ResponseEntity.ok(orderDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDto> getOrder(@PathVariable long id) {
        return ResponseEntity.ok(orderService.convertFromOrderById(id));
    }

    @GetMapping("/orders")
    public ResponseEntity<List<OrderDto>> orders() {
        return ResponseEntity.ok(orderService.getAllOrder());
    }
}
