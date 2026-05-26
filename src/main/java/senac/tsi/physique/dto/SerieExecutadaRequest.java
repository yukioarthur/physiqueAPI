package senac.tsi.physique.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(name = "SerieExecutadaRequest", description = "Série executada dentro da finalização de treino")
public class SerieExecutadaRequest {
    @Schema(description = "ID do exercício executado", example = "1")
    @NotNull(message = "O exercício é obrigatório")
    @Positive(message = "O ID do exercício deve ser maior que zero")
    private Long exercicioId;

    @Schema(description = "Número da série executada", example = "1")
    @NotNull(message = "O número da série é obrigatório")
    @Min(value = 1, message = "O número da série deve ser maior que zero")
    private Integer numeroSerie;

    @Schema(description = "Quantidade de repetições feitas", example = "10")
    @NotNull(message = "A quantidade de repetições é obrigatória")
    @Min(value = 1, message = "A quantidade de repetições deve ser maior que zero")
    private Integer repeticoes;

    @Schema(description = "Peso usado na série", example = "40.0")
    @NotNull(message = "O peso é obrigatório")
    @DecimalMin(value = "0.0", message = "O peso não pode ser negativo")
    private Double peso;

    public SerieExecutadaRequest() {}

    public Long getExercicioId() { return exercicioId; }
    public void setExercicioId(Long exercicioId) { this.exercicioId = exercicioId; }
    public Integer getNumeroSerie() { return numeroSerie; }
    public void setNumeroSerie(Integer numeroSerie) { this.numeroSerie = numeroSerie; }
    public Integer getRepeticoes() { return repeticoes; }
    public void setRepeticoes(Integer repeticoes) { this.repeticoes = repeticoes; }
    public Double getPeso() { return peso; }
    public void setPeso(Double peso) { this.peso = peso; }
}
