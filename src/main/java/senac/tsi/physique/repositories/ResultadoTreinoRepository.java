package senac.tsi.physique.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import senac.tsi.physique.entities.ResultadoTreino;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ResultadoTreinoRepository extends JpaRepository<ResultadoTreino, Long> {
    Page<ResultadoTreino> findByUsuarioId(Long usuarioId, Pageable pageable);
    List<ResultadoTreino> findByUsuarioIdAndDataBetweenAndStatusOrderByDataAsc(Long usuarioId, LocalDate inicio, LocalDate fim, String status);
    List<ResultadoTreino> findTop5ByUsuarioIdAndStatusOrderByDataDesc(Long usuarioId, String status);
}
