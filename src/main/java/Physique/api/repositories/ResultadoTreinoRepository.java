package Physique.api.repositories;

import Physique.api.entities.ResultadoTreino;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResultadoTreinoRepository extends JpaRepository<ResultadoTreino, Long> {
    Page<ResultadoTreino> findByUsuarioId(Long usuarioId, Pageable pageable);
}
