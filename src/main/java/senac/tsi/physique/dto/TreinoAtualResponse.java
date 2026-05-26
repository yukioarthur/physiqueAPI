package senac.tsi.physique.dto;

import java.util.List;

public class TreinoAtualResponse {
    private Long id;
    private String nome;
    private String objetivo;
    private String metodologia;
    private List<ExercicioTreinoResponse> exercicios;

    public TreinoAtualResponse() {}

    public TreinoAtualResponse(Long id, String nome, String objetivo, String metodologia, List<ExercicioTreinoResponse> exercicios) {
        this.id = id;
        this.nome = nome;
        this.objetivo = objetivo;
        this.metodologia = metodologia;
        this.exercicios = exercicios;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getObjetivo() { return objetivo; }
    public void setObjetivo(String objetivo) { this.objetivo = objetivo; }
    public String getMetodologia() { return metodologia; }
    public void setMetodologia(String metodologia) { this.metodologia = metodologia; }
    public List<ExercicioTreinoResponse> getExercicios() { return exercicios; }
    public void setExercicios(List<ExercicioTreinoResponse> exercicios) { this.exercicios = exercicios; }
}
