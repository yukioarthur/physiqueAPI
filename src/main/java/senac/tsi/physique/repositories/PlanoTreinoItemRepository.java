package senac.tsi.physique.repositories;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import senac.tsi.physique.entities.PlanoTreinoItem;

import java.util.List;

@Repository
public interface PlanoTreinoItemRepository extends JpaRepository<PlanoTreinoItem, Long> {
    @EntityGraph(attributePaths = {"treino"})
    List<PlanoTreinoItem> findAllByPlanoTreinoIdOrderByOrdemAsc(Long planoTreinoId);
}
