package senac.tsi.physique.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

@Schema(name = "ResultadoTreinoRequest", description = "Payload para registrar o resultado de uma sessão de treino por usuário")
public class ResultadoTreinoRequest {

    @Schema(description = "ID do treino executado", example = "1")
    @NotNull(message = "O treino é obrigatório")
    @Positive(message = "O ID do treino deve ser maior que zero")
    private Long treinoId;

    @Schema(description = "ID do usuário que executou o treino", example = "1")
    @NotNull(message = "O usuário é obrigatório")
    @Positive(message = "O ID do usuário deve ser maior que zero")
    private Long usuarioId;

    @Schema(description = "Data da sessão de treino", example = "2026-04-14")
    @NotNull(message = "A data do treino é obrigatória")
    @PastOrPresent(message = "A data do treino não pode ser futura")
    private LocalDate data;

    @Schema(description = "Resumo das séries realizadas", example = "1x10 50kg; 1x8 55kg; 1x6 60kg")
    @NotBlank(message = "A lista de séries e repetições é obrigatória")
    @Size(max = 500, message = "A lista de séries e repetições deve ter no máximo 500 caracteres")
    private String listaSerieRepeticao;

    @Schema(description = "Quantidade total de séries executadas no treino", example = "4")
    @NotNull(message = "A quantidade de séries é obrigatória")
    @Min(value = 1, message = "A quantidade de séries deve ser maior que zero")
    private Integer quantidadeSeriesTreino;

    @Schema(description = "Carga usada anteriormente em kg; a API usa esse valor para calcular o peso recomendado", example = "60.0")
    @NotNull(message = "O peso anterior é obrigatório")
    @DecimalMin(value = "0.1", message = "O peso anterior deve ser maior que zero")
    private Double pesoAnterior;

    public ResultadoTreinoRequest() {}

    public Long getTreinoId() { return treinoId; }
    public void setTreinoId(Long treinoId) { this.treinoId = treinoId; }
    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }
    public String getListaSerieRepeticao() { return listaSerieRepeticao; }
    public void setListaSerieRepeticao(String listaSerieRepeticao) { this.listaSerieRepeticao = listaSerieRepeticao; }
    public Integer getQuantidadeSeriesTreino() { return quantidadeSeriesTreino; }
    public void setQuantidadeSeriesTreino(Integer quantidadeSeriesTreino) { this.quantidadeSeriesTreino = quantidadeSeriesTreino; }
    public Double getPesoAnterior() { return pesoAnterior; }
    public void setPesoAnterior(Double pesoAnterior) { this.pesoAnterior = pesoAnterior; }
}
