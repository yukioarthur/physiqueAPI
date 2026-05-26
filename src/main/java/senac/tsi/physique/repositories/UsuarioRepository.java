package senac.tsi.physique.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import senac.tsi.physique.entities.Usuario;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Page<Usuario> findByObjetivoContainingIgnoreCase(String objetivo, Pageable pageable);
    Optional<Usuario> findByEmail(String email);
    boolean existsByEmail(String email);
}
