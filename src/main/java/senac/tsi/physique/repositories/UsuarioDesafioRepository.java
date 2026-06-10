package senac.tsi.physique.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import senac.tsi.physique.entities.UsuarioDesafio;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioDesafioRepository extends JpaRepository<UsuarioDesafio, Long> {
    List<UsuarioDesafio> findByUsuarioIdAndDesafioAtivoTrue(Long usuarioId);
    Optional<UsuarioDesafio> findByUsuarioIdAndDesafioId(Long usuarioId, Long desafioId);
    long countByUsuarioIdAndConcluidoTrue(Long usuarioId);
}
