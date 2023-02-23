package ru.trae.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.trae.backend.entity.task.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
}
