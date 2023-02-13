package ru.trae.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.trae.backend.entity.task.Operation;

@Repository
public interface OperationRepository extends JpaRepository<Operation, Long> {
}
