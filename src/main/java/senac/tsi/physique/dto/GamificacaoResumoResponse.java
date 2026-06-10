package senac.tsi.physique.dto;

public class GamificacaoResumoResponse {
    private Integer xpTotal;
    private Integer nivel;
    private Integer xpSemana;
    private Integer metaXpSemana;
    private Integer percentualSemana;
    private Integer desafiosConcluidos;
    private Integer desafiosTotal;
    private String tituloNivel;
    private String textoProgresso;

    public GamificacaoResumoResponse() {}

    public GamificacaoResumoResponse(Integer xpTotal, Integer nivel, Integer xpSemana, Integer metaXpSemana, Integer percentualSemana, Integer desafiosConcluidos, Integer desafiosTotal, String tituloNivel, String textoProgresso) {
        this.xpTotal = xpTotal;
        this.nivel = nivel;
        this.xpSemana = xpSemana;
        this.metaXpSemana = metaXpSemana;
        this.percentualSemana = percentualSemana;
        this.desafiosConcluidos = desafiosConcluidos;
        this.desafiosTotal = desafiosTotal;
        this.tituloNivel = tituloNivel;
        this.textoProgresso = textoProgresso;
    }

    public Integer getXpTotal() { return xpTotal; }
    public void setXpTotal(Integer xpTotal) { this.xpTotal = xpTotal; }
    public Integer getNivel() { return nivel; }
    public void setNivel(Integer nivel) { this.nivel = nivel; }
    public Integer getXpSemana() { return xpSemana; }
    public void setXpSemana(Integer xpSemana) { this.xpSemana = xpSemana; }
    public Integer getMetaXpSemana() { return metaXpSemana; }
    public void setMetaXpSemana(Integer metaXpSemana) { this.metaXpSemana = metaXpSemana; }
    public Integer getPercentualSemana() { return percentualSemana; }
    public void setPercentualSemana(Integer percentualSemana) { this.percentualSemana = percentualSemana; }
    public Integer getDesafiosConcluidos() { return desafiosConcluidos; }
    public void setDesafiosConcluidos(Integer desafiosConcluidos) { this.desafiosConcluidos = desafiosConcluidos; }
    public Integer getDesafiosTotal() { return desafiosTotal; }
    public void setDesafiosTotal(Integer desafiosTotal) { this.desafiosTotal = desafiosTotal; }
    public String getTituloNivel() { return tituloNivel; }
    public void setTituloNivel(String tituloNivel) { this.tituloNivel = tituloNivel; }
    public String getTextoProgresso() { return textoProgresso; }
    public void setTextoProgresso(String textoProgresso) { this.textoProgresso = textoProgresso; }
}
