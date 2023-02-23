package ru.trae.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.trae.backend.entity.PayloadRandomPiece;

import java.util.Optional;

@Repository
public interface PayloadRandomPieceRepository extends JpaRepository<PayloadRandomPiece, Long> {
    boolean existsByUsernameIgnoreCase(String username);

    @Transactional
    @Modifying
    @Query("update PayloadRandomPiece p set p.uuid = ?1 where upper(p.username) = upper(?2)")
    void updateUuidByUsernameIgnoreCase(String uuid, String username);

    Optional<PayloadRandomPiece> findByUsernameIgnoreCase(String username);

}
