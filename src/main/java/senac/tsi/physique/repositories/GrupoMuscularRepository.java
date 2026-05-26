package senac.tsi.physique.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import senac.tsi.physique.entities.GrupoMuscular;

@Repository
public interface GrupoMuscularRepository extends JpaRepository<GrupoMuscular, Long> {
    Page<GrupoMuscular> findByNomeContainingIgnoreCase(String nome, Pageable pageable);
}
