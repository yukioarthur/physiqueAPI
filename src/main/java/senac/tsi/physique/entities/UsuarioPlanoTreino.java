package senac.tsi.physique.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
@Table(name = "usuario_plano_treino")
public class UsuarioPlanoTreino {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "usuario_id")
    @NotNull
    private Usuario usuario;

    @ManyToOne(optional = false)
    @JoinColumn(name = "plano_treino_id")
    @NotNull
    private PlanoTreino planoTreino;

    @NotNull
    private Boolean ativo = true;

    @Column(name = "criado_em")
    @NotNull
    private LocalDateTime criadoEm = LocalDateTime.now();

    public UsuarioPlanoTreino() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public PlanoTreino getPlanoTreino() { return planoTreino; }
    public void setPlanoTreino(PlanoTreino planoTreino) { this.planoTreino = planoTreino; }
    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }
    public LocalDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(LocalDateTime criadoEm) { this.criadoEm = criadoEm; }
}
