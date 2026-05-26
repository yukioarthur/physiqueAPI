package senac.tsi.physique.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import senac.tsi.physique.entities.SerieExecutada;

@Repository
public interface SerieExecutadaRepository extends JpaRepository<SerieExecutada, Long> {
}
