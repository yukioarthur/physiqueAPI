package senac.tsi.physique.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import senac.tsi.physique.entities.Treino;

import java.util.Optional;

@Repository
public interface TreinoRepository extends JpaRepository<Treino, Long> {
    Page<Treino> findByMetodologiaContainingIgnoreCase(String metodologia, Pageable pageable);

    /**
     * Busca o treino carregando previamente os relacionamentos usados nas respostas versionadas.
     *
     * Isso evita LazyInitializationException/erro 500 no Render, onde spring.jpa.open-in-view=false.
     * O endpoint GET /treinos/{id} monta DTOs V1/V2 depois da consulta; por isso, os exercícios
     * e seus dados auxiliares precisam chegar carregados do repositório.
     */
    @EntityGraph(attributePaths = {
            "exercicios",
            "exercicios.grupoMuscular",
            "exercicios.musculo"
    })
    @Query("select t from Treino t where t.id = :id")
    Optional<Treino> findByIdComDetalhes(@Param("id") Long id);
}
