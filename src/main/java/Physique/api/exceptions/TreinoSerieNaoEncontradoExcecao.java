package Physique.api.exceptions;

public class TreinoSerieNaoEncontradoExcecao extends RuntimeException {
    public TreinoSerieNaoEncontradoExcecao(Long id) {
        super("Could not find TreinoSerie with id: " + id);
    }
}
