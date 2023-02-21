package ru.trae.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.trae.backend.entity.task.Project;

import java.time.LocalDateTime;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    @Transactional
    @Modifying
    @Query("update Project p set p.plannedEndDate = ?1 where p.id = ?2")
    void updatePlannedEndDateById(LocalDateTime plannedEndDate, Long id);
}
