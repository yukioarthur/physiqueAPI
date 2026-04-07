package Physique.api.repositories;

import Physique.api.entities.GrupoMuscular;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GrupoMuscularRepository extends JpaRepository<GrupoMuscular, Long> {
    Page<GrupoMuscular> findByNomeContainingIgnoreCase(String nome, Pageable pageable);
}
