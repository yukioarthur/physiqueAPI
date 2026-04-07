package Physique.api.repositories;

import Physique.api.entities.TreinoSerie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TreinoSerieRepository extends JpaRepository<TreinoSerie, Long> {
    Page<TreinoSerie> findByTreinoContainingIgnoreCase(String treino, Pageable pageable);
}
