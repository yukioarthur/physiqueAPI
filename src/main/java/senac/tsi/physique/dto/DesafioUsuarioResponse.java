package senac.tsi.physique.dto;

public class DesafioUsuarioResponse {
    private Long id;
    private String titulo;
    private String descricao;
    private String tipo;
    private String categoria;
    private Integer xp;
    private String regra;
    private Boolean manual;
    private String dica;
    private String status;
    private Integer progressoAtual;
    private Integer progressoMeta;
    private Boolean concluido;

    public DesafioUsuarioResponse() {}

    public DesafioUsuarioResponse(Long id, String titulo, String descricao, String tipo, String categoria, Integer xp, String regra, Boolean manual, String dica, String status, Integer progressoAtual, Integer progressoMeta, Boolean concluido) {
        this.id = id;
        this.titulo = titulo;
        this.descricao = descricao;
        this.tipo = tipo;
        this.categoria = categoria;
        this.xp = xp;
        this.regra = regra;
        this.manual = manual;
        this.dica = dica;
        this.status = status;
        this.progressoAtual = progressoAtual;
        this.progressoMeta = progressoMeta;
        this.concluido = concluido;
    }

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
    public Integer getXp() { return xp; }
    public void setXp(Integer xp) { this.xp = xp; }
    public String getRegra() { return regra; }
    public void setRegra(String regra) { this.regra = regra; }
    public Boolean getManual() { return manual; }
    public void setManual(Boolean manual) { this.manual = manual; }
    public String getDica() { return dica; }
    public void setDica(String dica) { this.dica = dica; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Integer getProgressoAtual() { return progressoAtual; }
    public void setProgressoAtual(Integer progressoAtual) { this.progressoAtual = progressoAtual; }
    public Integer getProgressoMeta() { return progressoMeta; }
    public void setProgressoMeta(Integer progressoMeta) { this.progressoMeta = progressoMeta; }
    public Boolean getConcluido() { return concluido; }
    public void setConcluido(Boolean concluido) { this.concluido = concluido; }
}
