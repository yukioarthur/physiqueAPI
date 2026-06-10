package senac.tsi.physique.repositories;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import senac.tsi.physique.entities.PlanoTreino;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlanoTreinoRepository extends JpaRepository<PlanoTreino, Long> {
    @EntityGraph(attributePaths = {"itens", "itens.treino"})
    List<PlanoTreino> findByAtivoTrueOrderByObjetivoAscOrdemAscNomeAsc();

    @EntityGraph(attributePaths = {"itens", "itens.treino"})
    Optional<PlanoTreino> findByIdAndAtivoTrue(Long id);
}
