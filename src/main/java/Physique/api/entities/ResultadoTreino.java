package Physique.api.entities;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

@Entity
@Schema(description = "Resultado de um treino executado por um usuário.")
public class ResultadoTreino {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador do resultado do treino.", example = "1")
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "treino_id")
    @NotNull
    @Schema(description = "Treino realizado.")
    private Treino treino;

    @ManyToOne(optional = false)
    @JoinColumn(name = "usuario_id")
    @NotNull
    @Schema(description = "Usuário que executou o treino.")
    private Usuario usuario;

    @NotNull
    @Schema(description = "Data do treino.", example = "2026-04-07")
    private LocalDate data;

    @NotBlank
    @Size(max = 500)
    @Schema(description = "Lista das séries, repetições e pesos executados em formato texto simples.", example = "S1: 10x40kg; S2: 8x45kg; S3: 6x50kg")
    private String listaSerieRepeticao;

    @NotNull
    @Min(1)
    @Schema(description = "Quantidade de séries planejadas para o treino.", example = "4")
    private Integer quantidadeSeriesTreino;

    @NotNull
    @DecimalMin("0.1")
    @Schema(description = "Peso anterior levantado na série de referência.", example = "80.0")
    private Double pesoAnterior;

    @Schema(description = "Peso recomendado calculado pela API.", example = "72.0")
    private Double pesoRecomendado;

    public ResultadoTreino() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Treino getTreino() { return treino; }
    public void setTreino(Treino treino) { this.treino = treino; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }
    public String getListaSerieRepeticao() { return listaSerieRepeticao; }
    public void setListaSerieRepeticao(String listaSerieRepeticao) { this.listaSerieRepeticao = listaSerieRepeticao; }
    public Integer getQuantidadeSeriesTreino() { return quantidadeSeriesTreino; }
    public void setQuantidadeSeriesTreino(Integer quantidadeSeriesTreino) { this.quantidadeSeriesTreino = quantidadeSeriesTreino; }
    public Double getPesoAnterior() { return pesoAnterior; }
    public void setPesoAnterior(Double pesoAnterior) { this.pesoAnterior = pesoAnterior; }
    public Double getPesoRecomendado() { return pesoRecomendado; }
    public void setPesoRecomendado(Double pesoRecomendado) { this.pesoRecomendado = pesoRecomendado; }
}
