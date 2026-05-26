package senac.tsi.physique.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import senac.tsi.physique.entities.UsuarioDesafio;

import java.util.List;

@Repository
public interface UsuarioDesafioRepository extends JpaRepository<UsuarioDesafio, Long> {
    List<UsuarioDesafio> findByUsuarioIdAndDesafioAtivoTrue(Long usuarioId);
}
