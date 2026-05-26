package senac.tsi.physique.dto;

public class FinalizarTreinoResponse {
    private Long resultadoTreinoId;
    private String message;

    public FinalizarTreinoResponse() {}

    public FinalizarTreinoResponse(Long resultadoTreinoId, String message) {
        this.resultadoTreinoId = resultadoTreinoId;
        this.message = message;
    }

    public Long getResultadoTreinoId() { return resultadoTreinoId; }
    public void setResultadoTreinoId(Long resultadoTreinoId) { this.resultadoTreinoId = resultadoTreinoId; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
