package Physique.api.entities;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

@Entity
@Schema(description = "Treino que agrupa exercícios e define um objetivo/metodologia.")
public class Treino {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador do treino.", example = "1")
    private Long id;

    @NotBlank
    @Size(max = 120)
    @Schema(description = "Nome do treino.", example = "Treino A")
    private String nome;

    @NotBlank
    @Size(max = 120)
    @Schema(description = "Objetivo do treino.", example = "Hipertrofia")
    private String objetivo;

    @NotBlank
    @Size(max = 120)
    @Schema(description = "Metodologia utilizada.", example = "Força")
    private String metodologia;

    @ManyToMany
    @JoinTable(
            name = "treino_exercicio",
            joinColumns = @JoinColumn(name = "treino_id"),
            inverseJoinColumns = @JoinColumn(name = "exercicio_id")
    )
    @Schema(description = "Lista de exercícios do treino.")
    private List<Exercicio> exercicios = new ArrayList<>();

    public Treino() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getObjetivo() { return objetivo; }
    public void setObjetivo(String objetivo) { this.objetivo = objetivo; }
    public String getMetodologia() { return metodologia; }
    public void setMetodologia(String metodologia) { this.metodologia = metodologia; }
    public List<Exercicio> getExercicios() { return exercicios; }
    public void setExercicios(List<Exercicio> exercicios) { this.exercicios = exercicios; }
}
