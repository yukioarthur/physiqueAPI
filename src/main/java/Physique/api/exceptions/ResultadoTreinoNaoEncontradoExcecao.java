package Physique.api.exceptions;

public class ResultadoTreinoNaoEncontradoExcecao extends RuntimeException {
    public ResultadoTreinoNaoEncontradoExcecao(Long id) {
        super("Could not find ResultadoTreino with id: " + id);
    }
}
