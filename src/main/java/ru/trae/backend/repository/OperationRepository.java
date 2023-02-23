package ru.trae.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.trae.backend.entity.task.Operation;

import java.util.List;

@Repository
public interface OperationRepository extends JpaRepository<Operation, Long> {

	List<Operation> findByReadyToAcceptanceAndTypeWork_Id(boolean readyToAcceptance, Long id);

}
