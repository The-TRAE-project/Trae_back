package ru.trae.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.trae.backend.entity.task.Operation;

import java.util.List;

@Repository
public interface OperationRepository extends JpaRepository<Operation, Long> {
    @Query("select o from Operation o where o.project.id = ?1")
    List<Operation> findByProjectId(long projectId);
}
