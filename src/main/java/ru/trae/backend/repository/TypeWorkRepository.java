package ru.trae.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.trae.backend.entity.TypeWork;

@Repository
public interface TypeWorkRepository extends JpaRepository<TypeWork, Long> {
    boolean existsByName(String name);

}
