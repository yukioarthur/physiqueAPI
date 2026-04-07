package Physique.api.exceptions;

public class MusculoNaoEncontradoExcecao extends RuntimeException {
    public MusculoNaoEncontradoExcecao(Long id) {
        super("Could not find Musculo with id: " + id);
    }
}
