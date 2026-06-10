package senac.tsi.physique.dto;

public class SeriePrescritaResponse {
    private Long id;
    private Integer numeroSerie;
    private Integer repeticoesMin;
    private Integer repeticoesMax;
    private Double cargaSugerida;
    private Integer rir;
    private Integer descansoSegundos;
    private String tempoExecucao;
    private String observacao;

    public SeriePrescritaResponse() {}

    public SeriePrescritaResponse(Long id,
                                  Integer numeroSerie,
                                  Integer repeticoesMin,
                                  Integer repeticoesMax,
                                  Double cargaSugerida,
                                  Integer rir,
                                  Integer descansoSegundos,
                                  String tempoExecucao,
                                  String observacao) {
        this.id = id;
        this.numeroSerie = numeroSerie;
        this.repeticoesMin = repeticoesMin;
        this.repeticoesMax = repeticoesMax;
        this.cargaSugerida = cargaSugerida;
        this.rir = rir;
        this.descansoSegundos = descansoSegundos;
        this.tempoExecucao = tempoExecucao;
        this.observacao = observacao;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Integer getNumeroSerie() { return numeroSerie; }
    public void setNumeroSerie(Integer numeroSerie) { this.numeroSerie = numeroSerie; }
    public Integer getRepeticoesMin() { return repeticoesMin; }
    public void setRepeticoesMin(Integer repeticoesMin) { this.repeticoesMin = repeticoesMin; }
    public Integer getRepeticoesMax() { return repeticoesMax; }
    public void setRepeticoesMax(Integer repeticoesMax) { this.repeticoesMax = repeticoesMax; }
    public Double getCargaSugerida() { return cargaSugerida; }
    public void setCargaSugerida(Double cargaSugerida) { this.cargaSugerida = cargaSugerida; }
    public Integer getRir() { return rir; }
    public void setRir(Integer rir) { this.rir = rir; }
    public Integer getDescansoSegundos() { return descansoSegundos; }
    public void setDescansoSegundos(Integer descansoSegundos) { this.descansoSegundos = descansoSegundos; }
    public String getTempoExecucao() { return tempoExecucao; }
    public void setTempoExecucao(String tempoExecucao) { this.tempoExecucao = tempoExecucao; }
    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }
}
