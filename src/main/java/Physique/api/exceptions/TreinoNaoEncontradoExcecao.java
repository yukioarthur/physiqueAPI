package Physique.api.exceptions;

public class TreinoNaoEncontradoExcecao extends RuntimeException {
    public TreinoNaoEncontradoExcecao(Long id) {
        super("Could not find Treino with id: " + id);
    }
}
