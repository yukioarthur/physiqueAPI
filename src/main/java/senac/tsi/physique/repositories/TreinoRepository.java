package senac.tsi.physique.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import senac.tsi.physique.entities.Treino;

@Repository
public interface TreinoRepository extends JpaRepository<Treino, Long> {
    Page<Treino> findByMetodologiaContainingIgnoreCase(String metodologia, Pageable pageable);
}
