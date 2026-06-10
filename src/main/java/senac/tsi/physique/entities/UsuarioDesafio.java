package senac.tsi.physique.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"usuario_id", "desafio_id"}))
public class UsuarioDesafio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "usuario_id")
    @NotNull
    private Usuario usuario;

    @ManyToOne(optional = false)
    @JoinColumn(name = "desafio_id")
    @NotNull
    private Desafio desafio;

    @NotNull
    @Min(0)
    private Integer progresso = 0;

    @NotNull
    @Min(1)
    private Integer progressoMeta = 1;

    @NotNull
    private Boolean concluido = false;

    @NotNull
    @Min(0)
    private Integer xpGanho = 0;

    @NotNull
    private String status = "PENDENTE";

    private LocalDateTime concluidoEm;

    public UsuarioDesafio() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public Desafio getDesafio() { return desafio; }
    public void setDesafio(Desafio desafio) { this.desafio = desafio; }
    public Integer getProgresso() { return progresso; }
    public void setProgresso(Integer progresso) { this.progresso = progresso; }
    public Integer getProgressoMeta() { return progressoMeta; }
    public void setProgressoMeta(Integer progressoMeta) { this.progressoMeta = progressoMeta; }
    public Boolean getConcluido() { return concluido; }
    public void setConcluido(Boolean concluido) { this.concluido = concluido; }
    public Integer getXpGanho() { return xpGanho; }
    public void setXpGanho(Integer xpGanho) { this.xpGanho = xpGanho; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getConcluidoEm() { return concluidoEm; }
    public void setConcluidoEm(LocalDateTime concluidoEm) { this.concluidoEm = concluidoEm; }
}
