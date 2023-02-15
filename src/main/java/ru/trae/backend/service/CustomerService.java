package ru.trae.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.trae.backend.dto.CustomerDto;
import ru.trae.backend.entity.user.Customer;
import ru.trae.backend.repository.CustomerRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;

    public Customer saveNewCustomer(CustomerDto dto) {
        Customer c = new Customer();
        c.setFirstName(dto.firstName());
        c.setMiddleName(dto.middleName());
        c.setLastName(dto.lastName());
        c.setPhone(dto.phone());
        c.setDateOfRegister(LocalDateTime.now());
        c.setOrders(new ArrayList<>());

        return customerRepository.save(c);
    }

    public Optional<Customer> getCustomer(String firstName, String middleName, String lastName) {
        return customerRepository.findByFirstNameIgnoreCaseAndMiddleNameIgnoreCaseAndLastNameIgnoreCase(firstName, middleName, lastName);
    }
}
