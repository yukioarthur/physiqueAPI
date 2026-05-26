package senac.tsi.physique.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Entity
public class SerieExecutada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "resultado_treino_id")
    @NotNull
    private ResultadoTreino resultadoTreino;

    @ManyToOne(optional = false)
    @JoinColumn(name = "exercicio_id")
    @NotNull
    private Exercicio exercicio;

    @NotNull
    @Min(1)
    private Integer numeroSerie;

    @NotNull
    @Min(1)
    private Integer repeticoes;

    @NotNull
    @DecimalMin("0.0")
    private Double peso;

    public SerieExecutada() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public ResultadoTreino getResultadoTreino() { return resultadoTreino; }
    public void setResultadoTreino(ResultadoTreino resultadoTreino) { this.resultadoTreino = resultadoTreino; }
    public Exercicio getExercicio() { return exercicio; }
    public void setExercicio(Exercicio exercicio) { this.exercicio = exercicio; }
    public Integer getNumeroSerie() { return numeroSerie; }
    public void setNumeroSerie(Integer numeroSerie) { this.numeroSerie = numeroSerie; }
    public Integer getRepeticoes() { return repeticoes; }
    public void setRepeticoes(Integer repeticoes) { this.repeticoes = repeticoes; }
    public Double getPeso() { return peso; }
    public void setPeso(Double peso) { this.peso = peso; }
}
