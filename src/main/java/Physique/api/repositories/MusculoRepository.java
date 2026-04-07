package Physique.api.repositories;

import Physique.api.entities.Musculo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MusculoRepository extends JpaRepository<Musculo, Long> {
    Page<Musculo> findByGrupoMuscularId(Long grupoMuscularId, Pageable pageable);
}
