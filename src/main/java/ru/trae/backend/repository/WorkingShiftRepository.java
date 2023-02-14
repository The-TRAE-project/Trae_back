package ru.trae.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.trae.backend.entity.WorkingShift;

@Repository
public interface WorkingShiftRepository extends JpaRepository<WorkingShift, Long> {
    boolean existsByIsEndedFalse();

    boolean existsByIsEndedFalseAndEmployees_PinCode(int pinCode);

    WorkingShift findByIsEndedFalse();

}
