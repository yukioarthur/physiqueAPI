package senac.tsi.physique.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import senac.tsi.physique.entities.UsuarioPlanoTreino;

import java.util.List;

@Repository
public interface UsuarioPlanoTreinoRepository extends JpaRepository<UsuarioPlanoTreino, Long> {
    List<UsuarioPlanoTreino> findAllByUsuarioIdAndAtivoTrue(Long usuarioId);
}
