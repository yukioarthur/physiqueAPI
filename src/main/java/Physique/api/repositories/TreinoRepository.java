package Physique.api.repositories;

import Physique.api.entities.Treino;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TreinoRepository extends JpaRepository<Treino, Long> {
    Page<Treino> findByMetodologiaContainingIgnoreCase(String metodologia, Pageable pageable);
}
