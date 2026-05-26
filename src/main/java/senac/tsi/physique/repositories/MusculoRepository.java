package senac.tsi.physique.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import senac.tsi.physique.entities.Musculo;

@Repository
public interface MusculoRepository extends JpaRepository<Musculo, Long> {
    Page<Musculo> findByGrupoMuscularId(Long grupoMuscularId, Pageable pageable);
}
