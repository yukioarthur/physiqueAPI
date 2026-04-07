package Physique.api.entities;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Schema(description = "Recurso simples para calcular 1RM e próxima série a partir de uma série informada pelo usuário.")
public class TreinoSerie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador da série calculada.", example = "1")
    private Long id;

    @NotNull
    @NotBlank
    @Size(min = 1, max = 255)
    @Schema(description = "Nome do treino ou exercício.", example = "Supino inclinado")
    private String treino;

    @NotNull
    @Min(1)
    @Schema(description = "Peso utilizado na série atual, em kg.", example = "70")
    private Double peso;

    @NotNull
    @Min(1)
    @Schema(description = "Repetições executadas na série atual.", example = "8")
    private Integer reps;

    @Schema(description = "Estimativa de uma repetição máxima (1RM).", example = "88.67")
    private Double umaRepMax;

    @Schema(description = "Peso recomendado para a próxima série.", example = "66.50")
    private Double proxSerieMax;

    @Schema(description = "Quantidade de repetições recomendada para a próxima série.", example = "8")
    private Integer proxSerieRep;

    public TreinoSerie() {
    }

    public TreinoSerie(String treino, Double peso, Integer reps) {
        this.treino = treino;
        this.peso = peso;
        this.reps = reps;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTreino() { return treino; }
    public void setTreino(String treino) { this.treino = treino; }
    public Double getPeso() { return peso; }
    public void setPeso(Double peso) { this.peso = peso; }
    public Integer getReps() { return reps; }
    public void setReps(Integer reps) { this.reps = reps; }
    public Double getUmaRepMax() { return umaRepMax; }
    public void setUmaRepMax(Double umaRepMax) { this.umaRepMax = umaRepMax; }
    public Double getProxSerieMax() { return proxSerieMax; }
    public void setProxSerieMax(Double proxSerieMax) { this.proxSerieMax = proxSerieMax; }
    public Integer getProxSerieRep() { return proxSerieRep; }
    public void setProxSerieRep(Integer proxSerieRep) { this.proxSerieRep = proxSerieRep; }
}
