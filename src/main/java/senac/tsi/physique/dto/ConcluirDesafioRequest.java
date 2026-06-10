package senac.tsi.physique.dto;

import jakarta.validation.constraints.NotNull;

public class ConcluirDesafioRequest {
    @NotNull
    private Boolean concluido;

    public ConcluirDesafioRequest() {}

    public Boolean getConcluido() { return concluido; }
    public void setConcluido(Boolean concluido) { this.concluido = concluido; }
}
