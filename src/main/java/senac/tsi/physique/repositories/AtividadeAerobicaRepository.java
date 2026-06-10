package senac.tsi.physique.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import senac.tsi.physique.entities.AtividadeAerobica;

import java.util.List;

@Repository
public interface AtividadeAerobicaRepository extends JpaRepository<AtividadeAerobica, Long> {
    List<AtividadeAerobica> findTop10ByUsuarioIdOrderByDataFimDesc(Long usuarioId);
}
