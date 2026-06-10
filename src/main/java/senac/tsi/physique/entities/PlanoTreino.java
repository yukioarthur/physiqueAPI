package senac.tsi.physique.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "plano_treino")
public class PlanoTreino {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 120)
    private String nome;

    @NotBlank
    @Size(max = 120)
    private String objetivo;

    @Size(max = 600)
    private String resumo;

    @Size(max = 80)
    private String nivel;

    @Column(name = "frequencia_semanal")
    @Size(max = 80)
    private String frequenciaSemanal;

    @Size(max = 160)
    private String foco;

    @Size(max = 500)
    private String tags;

    @Size(max = 500)
    private String metodologias;

    @NotNull
    private Integer ordem = 0;

    @NotNull
    private Boolean ativo = true;

    @OneToMany(mappedBy = "planoTreino", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlanoTreinoItem> itens = new ArrayList<>();

    public PlanoTreino() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getObjetivo() { return objetivo; }
    public void setObjetivo(String objetivo) { this.objetivo = objetivo; }
    public String getResumo() { return resumo; }
    public void setResumo(String resumo) { this.resumo = resumo; }
    public String getNivel() { return nivel; }
    public void setNivel(String nivel) { this.nivel = nivel; }
    public String getFrequenciaSemanal() { return frequenciaSemanal; }
    public void setFrequenciaSemanal(String frequenciaSemanal) { this.frequenciaSemanal = frequenciaSemanal; }
    public String getFoco() { return foco; }
    public void setFoco(String foco) { this.foco = foco; }
    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }
    public String getMetodologias() { return metodologias; }
    public void setMetodologias(String metodologias) { this.metodologias = metodologias; }
    public Integer getOrdem() { return ordem; }
    public void setOrdem(Integer ordem) { this.ordem = ordem; }
    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }
    public List<PlanoTreinoItem> getItens() { return itens; }
    public void setItens(List<PlanoTreinoItem> itens) { this.itens = itens; }
}
