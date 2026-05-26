package senac.tsi.physique.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(name = "TreinoSerieRequest", description = "Payload para calcular uma série estimada com base na carga e nas repetições")
public class TreinoSerieRequest {

    @Schema(description = "Nome livre da referência do treino ou exercício", example = "Supino reto")
    @NotBlank(message = "O nome do treino ou exercício é obrigatório")
    @Size(min = 1, max = 255, message = "O nome do treino ou exercício deve ter entre 1 e 255 caracteres")
    private String treino;

    @Schema(description = "Carga usada em kg", example = "60.0")
    @NotNull(message = "O peso é obrigatório")
    @DecimalMin(value = "0.1", message = "O peso deve ser maior que zero")
    private Double peso;

    @Schema(description = "Quantidade de repetições realizadas", example = "8")
    @NotNull(message = "A quantidade de repetições é obrigatória")
    @Min(value = 1, message = "A quantidade de repetições deve ser maior que zero")
    private Integer reps;

    public TreinoSerieRequest() {}

    public String getTreino() { return treino; }
    public void setTreino(String treino) { this.treino = treino; }
    public Double getPeso() { return peso; }
    public void setPeso(Double peso) { this.peso = peso; }
    public Integer getReps() { return reps; }
    public void setReps(Integer reps) { this.reps = reps; }
}
