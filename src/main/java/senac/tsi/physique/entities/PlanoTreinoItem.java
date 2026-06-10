package senac.tsi.physique.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "plano_treino_item")
public class PlanoTreinoItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "plano_treino_id")
    @NotNull
    private PlanoTreino planoTreino;

    @ManyToOne(optional = false)
    @JoinColumn(name = "treino_id")
    @NotNull
    private Treino treino;

    @NotNull
    private Integer ordem = 1;

    @Column(name = "nome_exibicao")
    private String nomeExibicao;

    public PlanoTreinoItem() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public PlanoTreino getPlanoTreino() { return planoTreino; }
    public void setPlanoTreino(PlanoTreino planoTreino) { this.planoTreino = planoTreino; }
    public Treino getTreino() { return treino; }
    public void setTreino(Treino treino) { this.treino = treino; }
    public Integer getOrdem() { return ordem; }
    public void setOrdem(Integer ordem) { this.ordem = ordem; }
    public String getNomeExibicao() { return nomeExibicao; }
    public void setNomeExibicao(String nomeExibicao) { this.nomeExibicao = nomeExibicao; }
}
