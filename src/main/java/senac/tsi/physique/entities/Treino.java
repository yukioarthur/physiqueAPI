package senac.tsi.physique.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Treino {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 120)
    private String nome;

    @NotBlank
    @Size(max = 120)
    private String objetivo;

    @NotBlank
    @Size(max = 120)
    private String metodologia;

    @Size(max = 120)
    private String criadorNome;

    @ManyToMany
    @JoinTable(
            name = "treino_exercicio",
            joinColumns = @JoinColumn(name = "treino_id"),
            inverseJoinColumns = @JoinColumn(name = "exercicio_id")
    )
    private List<Exercicio> exercicios = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "treino", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ResultadoTreino> resultadosTreino = new ArrayList<>();

    public Treino() {}
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getObjetivo() { return objetivo; }
    public void setObjetivo(String objetivo) { this.objetivo = objetivo; }
    public String getMetodologia() { return metodologia; }
    public void setMetodologia(String metodologia) { this.metodologia = metodologia; }
    public String getCriadorNome() { return criadorNome; }
    public void setCriadorNome(String criadorNome) { this.criadorNome = criadorNome; }
    public List<Exercicio> getExercicios() { return exercicios; }
    public void setExercicios(List<Exercicio> exercicios) { this.exercicios = exercicios; }
    public List<ResultadoTreino> getResultadosTreino() { return resultadosTreino; }
    public void setResultadosTreino(List<ResultadoTreino> resultadosTreino) { this.resultadosTreino = resultadosTreino; }
}
