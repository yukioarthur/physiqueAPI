package senac.tsi.physique.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import senac.tsi.physique.entities.TreinoSerie;

import java.util.List;

@Repository
public interface TreinoSerieRepository extends JpaRepository<TreinoSerie, Long> {
    Page<TreinoSerie> findByTreinoContainingIgnoreCase(String treino, Pageable pageable);

    @EntityGraph(attributePaths = {
            "treinoBase",
            "exercicio",
            "exercicio.grupoMuscular",
            "exercicio.musculo"
    })
    List<TreinoSerie> findByTreinoBaseIdOrderByOrdemExercicioAscNumeroSerieAsc(Long treinoId);
}
