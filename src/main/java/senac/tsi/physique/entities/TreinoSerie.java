package senac.tsi.physique.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "treino_serie")
public class TreinoSerie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Campo legado usado pelo endpoint /series-calculadas.
     * Mantido para compatibilidade com o CRUD existente do projeto.
     */
    @Size(max = 255)
    private String treino;

    @DecimalMin("0.1")
    private Double peso;

    @Min(1)
    private Integer reps;

    private Double umaRepMax;

    private Double proxSerieMax;

    private Integer proxSerieRep;

    /**
     * Campos novos: prescrição real de treino.
     * Um TreinoSerie agora também pode representar a série planejada de um exercício dentro de um treino.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "treino_id")
    private Treino treinoBase;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exercicio_id")
    private Exercicio exercicio;

    @Min(1)
    private Integer ordemExercicio;

    @Min(1)
    private Integer numeroSerie;

    @Min(1)
    private Integer repeticoesMin;

    @Min(1)
    private Integer repeticoesMax;

    @DecimalMin("0.0")
    private Double cargaSugerida;

    @Min(0)
    private Integer rir;

    @Min(0)
    private Integer descansoSegundos;

    @Size(max = 40)
    private String tempoExecucao;

    @Size(max = 500)
    private String observacao;

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

    public Treino getTreinoBase() { return treinoBase; }
    public void setTreinoBase(Treino treinoBase) { this.treinoBase = treinoBase; }

    public Exercicio getExercicio() { return exercicio; }
    public void setExercicio(Exercicio exercicio) { this.exercicio = exercicio; }

    public Integer getOrdemExercicio() { return ordemExercicio; }
    public void setOrdemExercicio(Integer ordemExercicio) { this.ordemExercicio = ordemExercicio; }

    public Integer getNumeroSerie() { return numeroSerie; }
    public void setNumeroSerie(Integer numeroSerie) { this.numeroSerie = numeroSerie; }

    public Integer getRepeticoesMin() { return repeticoesMin; }
    public void setRepeticoesMin(Integer repeticoesMin) { this.repeticoesMin = repeticoesMin; }

    public Integer getRepeticoesMax() { return repeticoesMax; }
    public void setRepeticoesMax(Integer repeticoesMax) { this.repeticoesMax = repeticoesMax; }

    public Double getCargaSugerida() { return cargaSugerida; }
    public void setCargaSugerida(Double cargaSugerida) { this.cargaSugerida = cargaSugerida; }

    public Integer getRir() { return rir; }
    public void setRir(Integer rir) { this.rir = rir; }

    public Integer getDescansoSegundos() { return descansoSegundos; }
    public void setDescansoSegundos(Integer descansoSegundos) { this.descansoSegundos = descansoSegundos; }

    public String getTempoExecucao() { return tempoExecucao; }
    public void setTempoExecucao(String tempoExecucao) { this.tempoExecucao = tempoExecucao; }

    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }
}
