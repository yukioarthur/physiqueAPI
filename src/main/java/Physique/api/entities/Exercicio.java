package Physique.api.entities;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Schema(description = "Exercício de um treino.")
public class Exercicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador do exercício.", example = "1")
    private Long id;

    @NotBlank
    @Size(max = 120)
    @Schema(description = "Nome do exercício.", example = "Supino reto")
    private String nome;

    @NotNull
    @Min(1)
    @Schema(description = "Repetições sugeridas.", example = "8")
    private Integer repeticoes;

    @NotNull
    @Min(1)
    @Schema(description = "Quantidade de séries.", example = "4")
    private Integer quantidadeSeries;

    @ManyToOne(optional = false)
    @JoinColumn(name = "grupo_muscular_id")
    @NotNull
    @Schema(description = "Grupo muscular principal do exercício.")
    private GrupoMuscular grupoMuscular;

    @ManyToOne(optional = false)
    @JoinColumn(name = "musculo_id")
    @NotNull
    @Schema(description = "Músculo principal do exercício.")
    private Musculo musculo;

    @Size(max = 500)
    @Schema(description = "Descrição do exercício.", example = "Desça a barra de forma controlada e empurre até a extensão dos cotovelos.")
    private String descricao;

    @Size(max = 255)
    @Schema(description = "Link do vídeo demonstrativo.", example = "")
    private String video;

    public Exercicio() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public Integer getRepeticoes() { return repeticoes; }
    public void setRepeticoes(Integer repeticoes) { this.repeticoes = repeticoes; }
    public Integer getQuantidadeSeries() { return quantidadeSeries; }
    public void setQuantidadeSeries(Integer quantidadeSeries) { this.quantidadeSeries = quantidadeSeries; }
    public GrupoMuscular getGrupoMuscular() { return grupoMuscular; }
    public void setGrupoMuscular(GrupoMuscular grupoMuscular) { this.grupoMuscular = grupoMuscular; }
    public Musculo getMusculo() { return musculo; }
    public void setMusculo(Musculo musculo) { this.musculo = musculo; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public String getVideo() { return video; }
    public void setVideo(String video) { this.video = video; }
}
