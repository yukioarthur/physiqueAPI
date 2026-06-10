package senac.tsi.physique.repositories;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import senac.tsi.physique.entities.UsuarioTreino;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioTreinoRepository extends JpaRepository<UsuarioTreino, Long> {

    @EntityGraph(attributePaths = {
            "treino",
            "treino.exercicios",
            "treino.exercicios.grupoMuscular",
            "treino.exercicios.musculo"
    })
    Optional<UsuarioTreino> findByUsuarioIdAndAtivoTrue(Long usuarioId);

    @EntityGraph(attributePaths = {
            "treino",
            "treino.exercicios",
            "treino.exercicios.grupoMuscular",
            "treino.exercicios.musculo"
    })
    Optional<UsuarioTreino> findByUsuarioIdAndTreinoId(Long usuarioId, Long treinoId);

    List<UsuarioTreino> findAllByUsuarioIdAndAtivoTrue(Long usuarioId);

    @EntityGraph(attributePaths = {"treino"})
    List<UsuarioTreino> findAllByUsuarioIdOrderByDataInicioAscIdAsc(Long usuarioId);
}
