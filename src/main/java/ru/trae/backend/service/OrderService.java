package ru.trae.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.trae.backend.dto.CustomerDto;
import ru.trae.backend.dto.NewOrderDto;
import ru.trae.backend.entity.task.Order;
import ru.trae.backend.entity.user.Customer;
import ru.trae.backend.entity.user.Manager;
import ru.trae.backend.repository.OrderRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final CustomerService customerService;

    public void receiveNewOrder(NewOrderDto dto, Manager m) {
        CustomerDto cDto = dto.customerDto();
        Customer c = customerService.getCustomer(cDto.firstName(), cDto.middleName(), cDto.lastName())
                .orElse(customerService.saveNewCustomer(dto.customerDto()));

        Order order = new Order();
        order.setName(dto.name());
        order.setDescription(dto.description());
        order.setEnded(false);
        order.setPeriod(dto.period());
        order.setStartDate(LocalDateTime.now());
        order.setManager(m);
        order.setCustomer(c);

        orderRepository.save(order);
    }

}
