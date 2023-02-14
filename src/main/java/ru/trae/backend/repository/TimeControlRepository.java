package ru.trae.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.trae.backend.entity.TimeControl;

public interface TimeControlRepository extends JpaRepository<TimeControl, Long> {

}
