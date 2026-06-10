package senac.tsi.physique.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import senac.tsi.physique.entities.AtividadeAerobicaPonto;

@Repository
public interface AtividadeAerobicaPontoRepository extends JpaRepository<AtividadeAerobicaPonto, Long> {
}
