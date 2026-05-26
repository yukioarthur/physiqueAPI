package senac.tsi.physique.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;
import java.util.List;

@Schema(name = "FinalizarTreinoRequest", description = "Payload para confirmar/finalizar um treino no app")
public class FinalizarTreinoRequest {
    @Schema(description = "ID do usuário que finalizou o treino", example = "1")
    @NotNull(message = "O usuário é obrigatório")
    @Positive(message = "O ID do usuário deve ser maior que zero")
    private Long usuarioId;

    @Schema(description = "ID do treino finalizado", example = "1")
    @NotNull(message = "O treino é obrigatório")
    @Positive(message = "O ID do treino deve ser maior que zero")
    private Long treinoId;

    @Schema(description = "Data da finalização do treino", example = "2026-05-25")
    @PastOrPresent(message = "A data do treino não pode ser futura")
    private LocalDate data;

    @Schema(description = "Séries executadas durante o treino")
    @NotEmpty(message = "Informe pelo menos uma série executada")
    @Valid
    private List<SerieExecutadaRequest> series;

    public FinalizarTreinoRequest() {}

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
    public Long getTreinoId() { return treinoId; }
    public void setTreinoId(Long treinoId) { this.treinoId = treinoId; }
    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }
    public List<SerieExecutadaRequest> getSeries() { return series; }
    public void setSeries(List<SerieExecutadaRequest> series) { this.series = series; }
}
