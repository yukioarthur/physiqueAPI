package senac.tsi.physique.dto;

import java.util.List;

public class PlanoTreinoCadastroResponse {
    private Long id;
    private String nome;
    private String objetivo;
    private String resumo;
    private String nivel;
    private String frequenciaSemanal;
    private String foco;
    private List<String> tags;
    private List<String> metodologias;
    private List<String> treinosIncluidos;

    public PlanoTreinoCadastroResponse() {}

    public PlanoTreinoCadastroResponse(Long id, String nome, String objetivo, String resumo, String nivel, String frequenciaSemanal, String foco, List<String> tags, List<String> metodologias, List<String> treinosIncluidos) {
        this.id = id;
        this.nome = nome;
        this.objetivo = objetivo;
        this.resumo = resumo;
        this.nivel = nivel;
        this.frequenciaSemanal = frequenciaSemanal;
        this.foco = foco;
        this.tags = tags;
        this.metodologias = metodologias;
        this.treinosIncluidos = treinosIncluidos;
    }

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
    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
    public List<String> getMetodologias() { return metodologias; }
    public void setMetodologias(List<String> metodologias) { this.metodologias = metodologias; }
    public List<String> getTreinosIncluidos() { return treinosIncluidos; }
    public void setTreinosIncluidos(List<String> treinosIncluidos) { this.treinosIncluidos = treinosIncluidos; }
}
