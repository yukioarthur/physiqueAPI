package senac.tsi.physique.dto.versioning;

import io.swagger.v3.oas.annotations.media.Schema;
import senac.tsi.physique.entities.Treino;

@Schema(name = "TreinoV1Response", description = "Representação compatível com a versão 1 do endpoint de treino")
public class TreinoV1Response {

    @Schema(description = "Versão da API usada para montar a resposta", example = "1")
    private String apiVersion;

    @Schema(description = "ID do treino", example = "1")
    private Long id;

    @Schema(description = "Nome do treino", example = "Treino A - Peito e Tríceps")
    private String nome;

    @Schema(description = "Objetivo do treino", example = "Hipertrofia")
    private String objetivo;

    @Schema(description = "Metodologia do treino", example = "Pirâmide crescente")
    private String metodologia;

    @Schema(description = "Nome do criador do treino", example = "Carlos Eduardo")
    private String criadorNome;

    public TreinoV1Response() {
    }

    public TreinoV1Response(String apiVersion, Long id, String nome, String objetivo, String metodologia, String criadorNome) {
        this.apiVersion = apiVersion;
        this.id = id;
        this.nome = nome;
        this.objetivo = objetivo;
        this.metodologia = metodologia;
        this.criadorNome = criadorNome;
    }

    public static TreinoV1Response from(Treino treino) {
        return new TreinoV1Response("1", treino.getId(), treino.getNome(), treino.getObjetivo(), treino.getMetodologia(), treino.getCriadorNome());
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
}
