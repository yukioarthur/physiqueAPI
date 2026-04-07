package Physique.api.entities;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Schema(description = "Grupo muscular do corpo, como Peito, Costas ou Pernas.")
public class GrupoMuscular {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador do grupo muscular.", example = "1")
    private Long id;

    @NotBlank
    @Size(max = 100)
    @Schema(description = "Nome do grupo muscular.", example = "Peito")
    private String nome;

    public GrupoMuscular() {}

    public GrupoMuscular(String nome) {
        this.nome = nome;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
}
