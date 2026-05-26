package senac.tsi.physique.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import senac.tsi.physique.entities.Exercicio;

@Repository
public interface ExercicioRepository extends JpaRepository<Exercicio, Long> {
    Page<Exercicio> findByNomeContainingIgnoreCase(String nome, Pageable pageable);
}
