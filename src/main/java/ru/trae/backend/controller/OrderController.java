package ru.trae.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.trae.backend.dto.order.NewOrderDto;
import ru.trae.backend.dto.order.OrderDto;
import ru.trae.backend.service.OrderService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/order")
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/new")
    public ResponseEntity<OrderDto> orderPersist(@RequestBody NewOrderDto dto) {
        OrderDto orderDto = orderService.convertFromOrder(orderService.receiveNewOrder(dto));
        return ResponseEntity.ok(orderDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDto> order(@PathVariable long id) {
        return ResponseEntity.ok(orderService.convertFromOrderById(id));
    }

    @GetMapping("/orders")
    public ResponseEntity<List<OrderDto>> orders() {
        return ResponseEntity.ok(orderService.getAllOrder());
    }
}
