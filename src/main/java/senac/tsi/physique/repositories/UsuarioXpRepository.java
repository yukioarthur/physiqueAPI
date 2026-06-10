package senac.tsi.physique.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import senac.tsi.physique.entities.UsuarioXp;

import java.util.Optional;

@Repository
public interface UsuarioXpRepository extends JpaRepository<UsuarioXp, Long> {
    Optional<UsuarioXp> findByUsuarioId(Long usuarioId);
}
