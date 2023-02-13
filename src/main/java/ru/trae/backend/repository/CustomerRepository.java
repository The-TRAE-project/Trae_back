package ru.trae.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.trae.backend.entity.user.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
