package senac.tsi.physique.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
public class Exercicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 120)
    private String nome;

    @NotNull
    @Min(1)
    private Integer repeticoes;

    @NotNull
    @Min(1)
    private Integer quantidadeSeries;

    @ManyToOne(optional = false)
    @JoinColumn(name = "grupo_muscular_id")
    @NotNull
    private GrupoMuscular grupoMuscular;

    @ManyToOne(optional = false)
    @JoinColumn(name = "musculo_id")
    @NotNull
    private Musculo musculo;

    @Size(max = 500)
    private String descricao;

    @Size(max = 255)
    private String video;

    public Exercicio() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Integer getRepeticoes() {
        return repeticoes;
    }

    public void setRepeticoes(Integer repeticoes) {
        this.repeticoes = repeticoes;
    }

    public Integer getQuantidadeSeries() {
        return quantidadeSeries;
    }

    public void setQuantidadeSeries(Integer quantidadeSeries) {
        this.quantidadeSeries = quantidadeSeries;
    }

    public GrupoMuscular getGrupoMuscular() {
        return grupoMuscular;
    }

    public void setGrupoMuscular(GrupoMuscular grupoMuscular) {
        this.grupoMuscular = grupoMuscular;
    }

    public Musculo getMusculo() {
        return musculo;
    }

    public void setMusculo(Musculo musculo) {
        this.musculo = musculo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }
}
