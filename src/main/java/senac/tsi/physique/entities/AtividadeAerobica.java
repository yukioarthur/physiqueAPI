package senac.tsi.physique.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Entity
@Table(name = "atividade_aerobica")
public class AtividadeAerobica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "usuario_id")
    @NotNull
    private Usuario usuario;

    @NotNull
    @Size(max = 60)
    private String tipo = "CAMINHADA";

    @NotNull
    private Double distanciaMetros = 0.0;

    @NotNull
    @Min(0)
    private Integer duracaoSegundos = 0;

    @NotNull
    @Min(0)
    private Integer xpGerado = 0;

    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;

    @Size(max = 500)
    private String observacao;

    public AtividadeAerobica() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public Double getDistanciaMetros() { return distanciaMetros; }
    public void setDistanciaMetros(Double distanciaMetros) { this.distanciaMetros = distanciaMetros; }
    public Integer getDuracaoSegundos() { return duracaoSegundos; }
    public void setDuracaoSegundos(Integer duracaoSegundos) { this.duracaoSegundos = duracaoSegundos; }
    public Integer getXpGerado() { return xpGerado; }
    public void setXpGerado(Integer xpGerado) { this.xpGerado = xpGerado; }
    public LocalDateTime getDataInicio() { return dataInicio; }
    public void setDataInicio(LocalDateTime dataInicio) { this.dataInicio = dataInicio; }
    public LocalDateTime getDataFim() { return dataFim; }
    public void setDataFim(LocalDateTime dataFim) { this.dataFim = dataFim; }
    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }
}
