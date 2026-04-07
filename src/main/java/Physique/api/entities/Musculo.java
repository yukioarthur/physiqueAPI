package Physique.api.entities;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Schema(description = "Músculo específico associado a um grupo muscular.")
public class Musculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador do músculo.", example = "1")
    private Long id;

    @NotBlank
    @Size(max = 100)
    @Schema(description = "Nome do músculo.", example = "Peitoral maior")
    private String nome;

    @ManyToOne(optional = false)
    @JoinColumn(name = "grupo_muscular_id")
    @NotNull
    @Schema(description = "Grupo muscular ao qual o músculo pertence.")
    private GrupoMuscular grupoMuscular;

    public Musculo() {}

    public Musculo(String nome, GrupoMuscular grupoMuscular) {
        this.nome = nome;
        this.grupoMuscular = grupoMuscular;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public GrupoMuscular getGrupoMuscular() { return grupoMuscular; }
    public void setGrupoMuscular(GrupoMuscular grupoMuscular) { this.grupoMuscular = grupoMuscular; }
}
