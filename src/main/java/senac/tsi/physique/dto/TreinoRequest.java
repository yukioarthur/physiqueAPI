package senac.tsi.physique.dto;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.List;

@Schema(name = "TreinoRequest", description = "Payload para criar ou atualizar um treino e associar exercícios já cadastrados")
public class TreinoRequest {

    @Schema(description = "Nome do treino", example = "Treino A - Peito e Tríceps")
    @NotBlank(message = "O nome do treino é obrigatório")
    @Size(max = 120, message = "O nome do treino deve ter no máximo 120 caracteres")
    private String nome;

    @Schema(description = "Objetivo do treino", example = "Hipertrofia")
    @NotBlank(message = "O objetivo do treino é obrigatório")
    @Size(max = 120, message = "O objetivo do treino deve ter no máximo 120 caracteres")
    private String objetivo;

    @Schema(description = "Metodologia usada no treino", example = "Pirâmide crescente")
    @NotBlank(message = "A metodologia do treino é obrigatória")
    @Size(max = 120, message = "A metodologia deve ter no máximo 120 caracteres")
    private String metodologia;

    @Schema(description = "Nome do criador do treino", example = "Carlos Eduardo")
    @Size(max = 120, message = "O nome do criador deve ter no máximo 120 caracteres")
    private String criadorNome;

    @ArraySchema(schema = @Schema(description = "IDs dos exercícios que compõem o treino", example = "1"))
    @NotEmpty(message = "Informe pelo menos um exercício para o treino")
    private List<@Positive(message = "O ID do exercício deve ser maior que zero") Long> exercicioIds;

    public TreinoRequest() {}

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getObjetivo() { return objetivo; }
    public void setObjetivo(String objetivo) { this.objetivo = objetivo; }
    public String getMetodologia() { return metodologia; }
    public void setMetodologia(String metodologia) { this.metodologia = metodologia; }
    public String getCriadorNome() { return criadorNome; }
    public void setCriadorNome(String criadorNome) { this.criadorNome = criadorNome; }
    public List<Long> getExercicioIds() { return exercicioIds; }
    public void setExercicioIds(List<Long> exercicioIds) { this.exercicioIds = exercicioIds; }
}
