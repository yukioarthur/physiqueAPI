package Physique.api.entities;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Schema(description = "Usuário/aluno da academia.")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador do usuário.", example = "1")
    private Long id;

    @NotBlank
    @Size(max = 120)
    @Schema(description = "Nome do usuário.", example = "Jorge Vieira")
    private String nome;

    @NotNull
    @Min(1)
    @Max(120)
    @Schema(description = "Idade do usuário.", example = "24")
    private Integer idade;

    @NotBlank
    @Size(max = 120)
    @Schema(description = "Objetivo do usuário.", example = "Hipertrofia")
    private String objetivo;

    @NotNull
    @DecimalMin("1.0")
    @Schema(description = "Peso corporal em kg.", example = "78.5")
    private Double pesoCorporal;

    public Usuario() {}

    public Usuario(String nome, Integer idade, String objetivo, Double pesoCorporal) {
        this.nome = nome;
        this.idade = idade;
        this.objetivo = objetivo;
        this.pesoCorporal = pesoCorporal;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public Integer getIdade() { return idade; }
    public void setIdade(Integer idade) { this.idade = idade; }
    public String getObjetivo() { return objetivo; }
    public void setObjetivo(String objetivo) { this.objetivo = objetivo; }
    public Double getPesoCorporal() { return pesoCorporal; }
    public void setPesoCorporal(Double pesoCorporal) { this.pesoCorporal = pesoCorporal; }
}
