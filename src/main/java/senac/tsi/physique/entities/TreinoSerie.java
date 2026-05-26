package senac.tsi.physique.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
public class TreinoSerie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 1, max = 255)
    private String treino;

    @NotNull
    @DecimalMin("0.1")
    private Double peso;

    @NotNull
    @Min(1)
    private Integer reps;

    private Double umaRepMax;

    private Double proxSerieMax;

    private Integer proxSerieRep;

    public TreinoSerie() {
    }

    public TreinoSerie(String treino, Double peso, Integer reps) {
        this.treino = treino;
        this.peso = peso;
        this.reps = reps;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTreino() {
        return treino;
    }

    public void setTreino(String treino) {
        this.treino = treino;
    }

    public Double getPeso() {
        return peso;
    }

    public void setPeso(Double peso) {
        this.peso = peso;
    }

    public Integer getReps() {
        return reps;
    }

    public void setReps(Integer reps) {
        this.reps = reps;
    }

    public Double getUmaRepMax() {
        return umaRepMax;
    }

    public void setUmaRepMax(Double umaRepMax) {
        this.umaRepMax = umaRepMax;
    }

    public Double getProxSerieMax() {
        return proxSerieMax;
    }

    public void setProxSerieMax(Double proxSerieMax) {
        this.proxSerieMax = proxSerieMax;
    }

    public Integer getProxSerieRep() {
        return proxSerieRep;
    }

    public void setProxSerieRep(Integer proxSerieRep) {
        this.proxSerieRep = proxSerieRep;
    }
}
