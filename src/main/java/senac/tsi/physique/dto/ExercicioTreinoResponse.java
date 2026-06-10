package senac.tsi.physique.dto;

import java.util.ArrayList;
import java.util.List;

public class ExercicioTreinoResponse {
    private Long id;
    private String nome;
    private Integer quantidadeSeries;
    private Integer repeticoes;
    private Integer repeticoesMin;
    private Integer repeticoesMax;
    private Integer descansoSegundos;
    private String grupoMuscular;
    private String musculo;
    private String descricao;
    private String observacao;
    private List<SeriePrescritaResponse> series = new ArrayList<>();

    public ExercicioTreinoResponse() {}

    public ExercicioTreinoResponse(Long id, String nome, Integer quantidadeSeries, Integer repeticoes, String grupoMuscular, String musculo) {
        this(id, nome, quantidadeSeries, repeticoes, repeticoes, repeticoes, null, grupoMuscular, musculo, null, null, new ArrayList<>());
    }

    public ExercicioTreinoResponse(Long id,
                                   String nome,
                                   Integer quantidadeSeries,
                                   Integer repeticoes,
                                   Integer repeticoesMin,
                                   Integer repeticoesMax,
                                   Integer descansoSegundos,
                                   String grupoMuscular,
                                   String musculo,
                                   String descricao,
                                   String observacao,
                                   List<SeriePrescritaResponse> series) {
        this.id = id;
        this.nome = nome;
        this.quantidadeSeries = quantidadeSeries;
        this.repeticoes = repeticoes;
        this.repeticoesMin = repeticoesMin;
        this.repeticoesMax = repeticoesMax;
        this.descansoSegundos = descansoSegundos;
        this.grupoMuscular = grupoMuscular;
        this.musculo = musculo;
        this.descricao = descricao;
        this.observacao = observacao;
        this.series = series == null ? new ArrayList<>() : series;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public Integer getQuantidadeSeries() { return quantidadeSeries; }
    public void setQuantidadeSeries(Integer quantidadeSeries) { this.quantidadeSeries = quantidadeSeries; }
    public Integer getRepeticoes() { return repeticoes; }
    public void setRepeticoes(Integer repeticoes) { this.repeticoes = repeticoes; }
    public Integer getRepeticoesMin() { return repeticoesMin; }
    public void setRepeticoesMin(Integer repeticoesMin) { this.repeticoesMin = repeticoesMin; }
    public Integer getRepeticoesMax() { return repeticoesMax; }
    public void setRepeticoesMax(Integer repeticoesMax) { this.repeticoesMax = repeticoesMax; }
    public Integer getDescansoSegundos() { return descansoSegundos; }
    public void setDescansoSegundos(Integer descansoSegundos) { this.descansoSegundos = descansoSegundos; }
    public String getGrupoMuscular() { return grupoMuscular; }
    public void setGrupoMuscular(String grupoMuscular) { this.grupoMuscular = grupoMuscular; }
    public String getMusculo() { return musculo; }
    public void setMusculo(String musculo) { this.musculo = musculo; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }
    public List<SeriePrescritaResponse> getSeries() { return series; }
    public void setSeries(List<SeriePrescritaResponse> series) { this.series = series; }
}
