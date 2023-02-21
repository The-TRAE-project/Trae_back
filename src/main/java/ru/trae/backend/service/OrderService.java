package ru.trae.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.trae.backend.dto.CustomerDto;
import ru.trae.backend.dto.order.NewOrderDto;
import ru.trae.backend.dto.order.OrderDto;
import ru.trae.backend.dto.mapper.OrderDtoMapper;
import ru.trae.backend.entity.task.Order;
import ru.trae.backend.entity.user.Customer;
import ru.trae.backend.exceptionhandler.exception.OrderException;
import ru.trae.backend.repository.OrderRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final CustomerService customerService;
    private final ManagerService managerService;
    private final OrderDtoMapper orderDtoMapper;

    public Order receiveNewOrder(NewOrderDto dto) {
        CustomerDto cDto = dto.customerDto();
        Customer c = customerService.getCustomer(cDto.firstName(), cDto.middleName(), cDto.lastName())
                .orElse(customerService.saveNewCustomer(dto.customerDto()));

        Order order = new Order();
        order.setName(dto.name());
        order.setDescription(dto.description());
        order.setEnded(false);
        order.setPeriod(dto.period());
        order.setStartDate(LocalDateTime.now());
        order.setPlannedEndDate(LocalDateTime.now().plusDays(order.getPeriod()));
        order.setRealEndDate(null);
        order.setManager(managerService.getManagerById(dto.managerId()));
        order.setCustomer(c);

        return orderRepository.save(order);
    }

    public Order getOrderById(long id) {
        return orderRepository.findById(id).orElseThrow(
                () -> new OrderException(HttpStatus.NOT_FOUND, "The order with ID: " + id + " not found"));
    }

    public List<OrderDto> getAllOrder() {
        return orderRepository.findAll()
                .stream()
                .map(orderDtoMapper)
                .toList();
    }

    public OrderDto convertFromOrderById(long id) {
        return orderDtoMapper.apply(getOrderById(id));
    }

    public OrderDto convertFromOrder(Order o) {
        return orderDtoMapper.apply(o);
    }
}
