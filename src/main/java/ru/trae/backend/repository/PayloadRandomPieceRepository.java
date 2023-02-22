package ru.trae.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.trae.backend.entity.PayloadRandomPiece;

import java.util.Optional;

@Repository
public interface PayloadRandomPieceRepository extends JpaRepository<PayloadRandomPiece, Long> {
    Optional<PayloadRandomPiece> findByUsernameIgnoreCase(String username);

}
