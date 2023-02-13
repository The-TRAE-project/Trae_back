package ru.trae.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.trae.backend.entity.user.Employee;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
}
