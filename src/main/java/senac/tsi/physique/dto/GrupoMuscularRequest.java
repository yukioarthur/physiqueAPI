package senac.tsi.physique.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(name = "GrupoMuscularRequest", description = "Payload para criar ou atualizar um grupo muscular")
public class GrupoMuscularRequest {

    @Schema(description = "Nome do grupo muscular", example = "Peito")
    @NotBlank(message = "O nome do grupo muscular é obrigatório")
    @Size(max = 100, message = "O nome do grupo muscular deve ter no máximo 100 caracteres")
    private String nome;

    public GrupoMuscularRequest() {}

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
}
