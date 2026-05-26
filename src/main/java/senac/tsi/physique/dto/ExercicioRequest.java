package senac.tsi.physique.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@Schema(name = "ExercicioRequest", description = "Payload para criar ou atualizar um exercício")
public class ExercicioRequest {

    @Schema(description = "Nome do exercício", example = "Supino reto")
    @NotBlank(message = "O nome do exercício é obrigatório")
    @Size(max = 120, message = "O nome do exercício deve ter no máximo 120 caracteres")
    private String nome;

    @Schema(description = "Quantidade alvo de repetições por série", example = "10")
    @NotNull(message = "A quantidade de repetições é obrigatória")
    @Min(value = 1, message = "A quantidade de repetições deve ser maior que zero")
    private Integer repeticoes;

    @Schema(description = "Quantidade de séries do exercício", example = "4")
    @NotNull(message = "A quantidade de séries é obrigatória")
    @Min(value = 1, message = "A quantidade de séries deve ser maior que zero")
    private Integer quantidadeSeries;

    @Schema(description = "ID do grupo muscular principal do exercício", example = "1")
    @NotNull(message = "O grupo muscular é obrigatório")
    @Positive(message = "O ID do grupo muscular deve ser maior que zero")
    private Long grupoMuscularId;

    @Schema(description = "ID do músculo principal do exercício", example = "1")
    @NotNull(message = "O músculo é obrigatório")
    @Positive(message = "O ID do músculo deve ser maior que zero")
    private Long musculoId;

    @Schema(description = "Descrição livre do exercício", example = "Descer a barra até a linha média do peito com controle.")
    @Size(max = 500, message = "A descrição deve ter no máximo 500 caracteres")
    private String descricao;

    @Schema(description = "URL de vídeo de apoio", example = "https://meus-videos.dev/supino-reto")
    @Size(max = 255, message = "A URL do vídeo deve ter no máximo 255 caracteres")
    private String video;

    public ExercicioRequest() {}

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public Integer getRepeticoes() { return repeticoes; }
    public void setRepeticoes(Integer repeticoes) { this.repeticoes = repeticoes; }
    public Integer getQuantidadeSeries() { return quantidadeSeries; }
    public void setQuantidadeSeries(Integer quantidadeSeries) { this.quantidadeSeries = quantidadeSeries; }
    public Long getGrupoMuscularId() { return grupoMuscularId; }
    public void setGrupoMuscularId(Long grupoMuscularId) { this.grupoMuscularId = grupoMuscularId; }
    public Long getMusculoId() { return musculoId; }
    public void setMusculoId(Long musculoId) { this.musculoId = musculoId; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public String getVideo() { return video; }
    public void setVideo(String video) { this.video = video; }
}
