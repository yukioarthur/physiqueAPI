package senac.tsi.physique.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
public class Desafio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 160)
    private String titulo;

    @Size(max = 500)
    private String descricao;

    @NotBlank
    @Size(max = 80)
    private String tipo;

    @Size(max = 80)
    private String categoria = "TREINO";

    @NotNull
    @Min(1)
    private Integer meta = 1;

    @NotNull
    @Min(0)
    private Integer xp = 80;

    @NotNull
    @DecimalMin("0.1")
    private Double xpMultiplicador = 1.0;

    @Size(max = 80)
    private String regra;

    @NotNull
    private Boolean manual = false;

    @Size(max = 500)
    private String dica;

    @NotNull
    private Boolean ativo = true;

    public Desafio() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public Integer getMeta() { return meta; }
    public void setMeta(Integer meta) { this.meta = meta; }
    public Integer getXp() { return xp; }
    public void setXp(Integer xp) { this.xp = xp; }
    public Double getXpMultiplicador() { return xpMultiplicador; }
    public void setXpMultiplicador(Double xpMultiplicador) { this.xpMultiplicador = xpMultiplicador; }
    public String getRegra() { return regra; }
    public void setRegra(String regra) { this.regra = regra; }
    public Boolean getManual() { return manual; }
    public void setManual(Boolean manual) { this.manual = manual; }
    public String getDica() { return dica; }
    public void setDica(String dica) { this.dica = dica; }
    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }

    @Transient
    public int getXpAplicado() {
        return (int) Math.round((xp == null ? 0 : xp) * (xpMultiplicador == null ? 1.0 : xpMultiplicador));
    }
}
