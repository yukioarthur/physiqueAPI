package senac.tsi.physique.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
@Table(name = "usuario_xp", uniqueConstraints = @UniqueConstraint(columnNames = "usuario_id"))
public class UsuarioXp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "usuario_id")
    @NotNull
    private Usuario usuario;

    @NotNull
    @Min(0)
    private Integer xpTotal = 0;

    @NotNull
    @Min(1)
    private Integer nivel = 1;

    @NotNull
    @Min(0)
    private Integer xpSemana = 0;

    @NotNull
    @Min(1)
    private Integer metaXpSemana = 1000;

    @NotNull
    @Min(0)
    private Integer desafiosConcluidos = 0;

    @NotNull
    private LocalDateTime atualizadoEm = LocalDateTime.now();

    public UsuarioXp() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public Integer getXpTotal() { return xpTotal; }
    public void setXpTotal(Integer xpTotal) { this.xpTotal = xpTotal; }
    public Integer getNivel() { return nivel; }
    public void setNivel(Integer nivel) { this.nivel = nivel; }
    public Integer getXpSemana() { return xpSemana; }
    public void setXpSemana(Integer xpSemana) { this.xpSemana = xpSemana; }
    public Integer getMetaXpSemana() { return metaXpSemana; }
    public void setMetaXpSemana(Integer metaXpSemana) { this.metaXpSemana = metaXpSemana; }
    public Integer getDesafiosConcluidos() { return desafiosConcluidos; }
    public void setDesafiosConcluidos(Integer desafiosConcluidos) { this.desafiosConcluidos = desafiosConcluidos; }
    public LocalDateTime getAtualizadoEm() { return atualizadoEm; }
    public void setAtualizadoEm(LocalDateTime atualizadoEm) { this.atualizadoEm = atualizadoEm; }
}
