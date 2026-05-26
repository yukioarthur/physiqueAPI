package senac.tsi.physique.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import senac.tsi.physique.entities.TreinoSerie;

@Repository
public interface TreinoSerieRepository extends JpaRepository<TreinoSerie, Long> {
    Page<TreinoSerie> findByTreinoContainingIgnoreCase(String treino, Pageable pageable);
}
