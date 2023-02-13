package ru.trae.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.trae.backend.entity.WorkShifting;

@Repository
public interface WorkShiftingRepository extends JpaRepository<WorkShifting, Long> {
}
