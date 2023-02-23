package ru.trae.backend.dto.mapper;

import org.springframework.stereotype.Service;
import ru.trae.backend.dto.CustomerDto;
import ru.trae.backend.entity.user.Customer;

import java.util.function.Function;
@Service
public class CustomerDtoMapper implements Function<Customer, CustomerDto> {
    @Override
    public CustomerDto apply(Customer c) {
        return new CustomerDto(
                c.getFirstName(),
                c.getMiddleName(),
                c.getLastName(),
                c.getPhone(),
                c.getDateOfRegister()
        );
    }
}
