package ru.trae.backend.dto.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.trae.backend.dto.order.OrderDto;
import ru.trae.backend.entity.task.Order;

import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class OrderDtoMapper implements Function<Order, OrderDto> {
    private final CustomerDtoMapper customerDtoMapper;
    private final ManagerDtoMapper managerDtoMapper;

    @Override
    public OrderDto apply(Order o) {
        return new OrderDto(
                o.getId(),
                o.getName(),
                o.getDescription(),
                o.getStartDate(),
                o.getEndDate(),
                o.getPeriod(),
                o.isEnded(),
                customerDtoMapper.apply(o.getCustomer()),
                managerDtoMapper.apply(o.getManager())
        );
    }
}
