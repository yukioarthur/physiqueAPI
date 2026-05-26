package senac.tsi.physique.dto;

public class ExercicioTreinoResponse {
    private Long id;
    private String nome;
    private Integer quantidadeSeries;
    private Integer repeticoes;
    private String grupoMuscular;
    private String musculo;

    public ExercicioTreinoResponse() {}

    public ExercicioTreinoResponse(Long id, String nome, Integer quantidadeSeries, Integer repeticoes, String grupoMuscular, String musculo) {
        this.id = id;
        this.nome = nome;
        this.quantidadeSeries = quantidadeSeries;
        this.repeticoes = repeticoes;
        this.grupoMuscular = grupoMuscular;
        this.musculo = musculo;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public Integer getQuantidadeSeries() { return quantidadeSeries; }
    public void setQuantidadeSeries(Integer quantidadeSeries) { this.quantidadeSeries = quantidadeSeries; }
    public Integer getRepeticoes() { return repeticoes; }
    public void setRepeticoes(Integer repeticoes) { this.repeticoes = repeticoes; }
    public String getGrupoMuscular() { return grupoMuscular; }
    public void setGrupoMuscular(String grupoMuscular) { this.grupoMuscular = grupoMuscular; }
    public String getMusculo() { return musculo; }
    public void setMusculo(String musculo) { this.musculo = musculo; }
}
