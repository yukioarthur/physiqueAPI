package senac.tsi.physique.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@Schema(name = "MusculoRequest", description = "Payload para criar ou atualizar um músculo vinculado a um grupo muscular já existente")
public class MusculoRequest {

    @Schema(description = "Nome do músculo", example = "Peitoral maior")
    @NotBlank(message = "O nome do músculo é obrigatório")
    @Size(max = 100, message = "O nome do músculo deve ter no máximo 100 caracteres")
    private String nome;

    @Schema(description = "ID do grupo muscular ao qual o músculo pertence", example = "1")
    @NotNull(message = "O grupo muscular é obrigatório")
    @Positive(message = "O ID do grupo muscular deve ser maior que zero")
    private Long grupoMuscularId;

    public MusculoRequest() {}

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public Long getGrupoMuscularId() { return grupoMuscularId; }
    public void setGrupoMuscularId(Long grupoMuscularId) { this.grupoMuscularId = grupoMuscularId; }
}
