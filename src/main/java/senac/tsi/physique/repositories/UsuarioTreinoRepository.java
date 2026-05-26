package senac.tsi.physique.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import senac.tsi.physique.entities.UsuarioTreino;

import java.util.Optional;

@Repository
public interface UsuarioTreinoRepository extends JpaRepository<UsuarioTreino, Long> {
    Optional<UsuarioTreino> findByUsuarioIdAndAtivoTrue(Long usuarioId);
}
