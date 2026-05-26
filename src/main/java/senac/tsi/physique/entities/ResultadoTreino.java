package senac.tsi.physique.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

@Entity
public class ResultadoTreino {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "treino_id")
    @NotNull
    private Treino treino;

    @ManyToOne(optional = false)
    @JoinColumn(name = "usuario_id")
    @NotNull
    private Usuario usuario;

    @NotNull
    private LocalDate data;

    @Size(max = 500)
    private String listaSerieRepeticao;

    @NotNull
    @Min(1)
    private Integer quantidadeSeriesTreino;

    @DecimalMin("0.0")
    private Double pesoAnterior;

    private Double pesoRecomendado;

    private Double volumeTotal = 0.0;

    @Size(max = 30)
    private String status = "FINALIZADO";

    public ResultadoTreino() {
    }

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
    public Double getVolumeTotal() { return volumeTotal; }
    public void setVolumeTotal(Double volumeTotal) { this.volumeTotal = volumeTotal; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
