package senac.tsi.physique.dto.versioning;

import io.swagger.v3.oas.annotations.media.Schema;
import senac.tsi.physique.entities.Exercicio;
import senac.tsi.physique.entities.Treino;

import java.util.List;
import java.util.Map;

@Schema(name = "TreinoV2Response", description = "Representação evoluída da versão 2 do endpoint de treino")
public class TreinoV2Response {

    @Schema(description = "Versão da API usada para montar a resposta", example = "2")
    private String apiVersion;

    private Long id;
    private String nome;
    private String objetivo;
    private String metodologia;
    private String criadorNome;
    private Integer quantidadeExercicios;
    private List<ExercicioResumo> exercicios;
    private Map<String, String> links;

    public TreinoV2Response() {
    }

    public TreinoV2Response(String apiVersion,
                            Long id,
                            String nome,
                            String objetivo,
                            String metodologia,
                            String criadorNome,
                            Integer quantidadeExercicios,
                            List<ExercicioResumo> exercicios,
                            Map<String, String> links) {
        this.apiVersion = apiVersion;
        this.id = id;
        this.nome = nome;
        this.objetivo = objetivo;
        this.metodologia = metodologia;
        this.criadorNome = criadorNome;
        this.quantidadeExercicios = quantidadeExercicios;
        this.exercicios = exercicios;
        this.links = links;
    }

    public static TreinoV2Response from(Treino treino) {
        List<ExercicioResumo> exercicios = treino.getExercicios() == null
                ? List.of()
                : treino.getExercicios().stream().map(ExercicioResumo::from).toList();
        return new TreinoV2Response(
                "2",
                treino.getId(),
                treino.getNome(),
                treino.getObjetivo(),
                treino.getMetodologia(),
                treino.getCriadorNome(),
                exercicios.size(),
                exercicios,
                Map.of(
                        "self", "/treinos/" + treino.getId(),
                        "treinos", "/treinos"
                )
        );
    }

    public String getApiVersion() { return apiVersion; }
    public void setApiVersion(String apiVersion) { this.apiVersion = apiVersion; }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getObjetivo() { return objetivo; }
    public void setObjetivo(String objetivo) { this.objetivo = objetivo; }
    public String getMetodologia() { return metodologia; }
    public void setMetodologia(String metodologia) { this.metodologia = metodologia; }
    public String getCriadorNome() { return criadorNome; }
    public void setCriadorNome(String criadorNome) { this.criadorNome = criadorNome; }
    public Integer getQuantidadeExercicios() { return quantidadeExercicios; }
    public void setQuantidadeExercicios(Integer quantidadeExercicios) { this.quantidadeExercicios = quantidadeExercicios; }
    public List<ExercicioResumo> getExercicios() { return exercicios; }
    public void setExercicios(List<ExercicioResumo> exercicios) { this.exercicios = exercicios; }
    public Map<String, String> getLinks() { return links; }
    public void setLinks(Map<String, String> links) { this.links = links; }

    @Schema(name = "TreinoV2ExercicioResumo", description = "Resumo de exercício dentro da resposta V2 de treino")
    public static class ExercicioResumo {
        private Long id;
        private String nome;
        private Integer quantidadeSeries;
        private Integer repeticoes;
        private String grupoMuscular;
        private String musculo;

        public ExercicioResumo() {
        }

        public ExercicioResumo(Long id, String nome, Integer quantidadeSeries, Integer repeticoes, String grupoMuscular, String musculo) {
            this.id = id;
            this.nome = nome;
            this.quantidadeSeries = quantidadeSeries;
            this.repeticoes = repeticoes;
            this.grupoMuscular = grupoMuscular;
            this.musculo = musculo;
        }

        public static ExercicioResumo from(Exercicio exercicio) {
            return new ExercicioResumo(
                    exercicio.getId(),
                    exercicio.getNome(),
                    exercicio.getQuantidadeSeries(),
                    exercicio.getRepeticoes(),
                    exercicio.getGrupoMuscular() == null ? null : exercicio.getGrupoMuscular().getNome(),
                    exercicio.getMusculo() == null ? null : exercicio.getMusculo().getNome()
            );
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
}
