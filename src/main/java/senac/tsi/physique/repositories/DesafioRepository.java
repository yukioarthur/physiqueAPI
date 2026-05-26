package senac.tsi.physique.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import senac.tsi.physique.entities.Desafio;

import java.util.List;

@Repository
public interface DesafioRepository extends JpaRepository<Desafio, Long> {
    List<Desafio> findByAtivoTrue();
}
